elastic-job是当当开源的一款非常好用的作业框架，在这之前，我们开发定时任务一般都是使用quartz或者spring-task（ScheduledExecutorService），无论是使用quartz还是spring-task，我们都会至少遇到两个痛点：
1.不敢轻易跟着应用服务多节点部署，可能会重复多次执行而引发系统逻辑的错误。
2.quartz的集群仅仅只是用来HA，节点数量的增加并不能给我们的每次执行效率带来提升，即不能实现水平扩展。
本篇博文将会自顶向下地介绍elastic-job，让大家认识了解并且快速搭建起环境。

elastic-job产品线说明
elastic-job在2.x之后，出了两个产品线：Elastic-Job-Lite和Elastic-Job-Cloud。我们一般使用Elastic-Job-Lite就能够满足需求，本文也是以Elastic-Job-Lite为主。1.x系列对应的就只有Elastic-Job-Lite，并且在2.x里修改了一些核心类名，差别虽大，原理类似，建议使用2.x系列。写此博文，最新release版本为2.0.5。


elastic-job-lite原理
举个典型的job场景，比如余额宝里的昨日收益，系统需要job在每天某个时间点开始，给所有余额宝用户计算收益。如果用户数量不多，我们可以轻易使用quartz来完成，我们让计息job在某个时间点开始执行，循环遍历所有用户计算利息，这没问题。可是，如果用户体量特别大，我们可能会面临着在第二天之前处理不完这么多用户。另外，我们部署job的时候也得注意，我们可能会把job直接放在我们的webapp里，webapp通常是多节点部署的，这样，我们的job也就是多节点，多个job同时执行，很容易造成重复执行，比如用户重复计息，为了避免这种情况，我们可能会对job的执行加锁，保证始终只有一个节点能执行，或者干脆让job从webapp里剥离出来，独自部署一个节点。
elastic-job就可以帮助我们解决上面的问题，elastic底层的任务调度还是使用的quartz，通过zookeeper来动态给job节点分片。
我们来看：
很大体量的用户需要在特定的时间段内计息完成
我们肯定是希望我们的任务可以通过集群达到水平扩展，集群里的每个节点都处理部分用户，不管用户数量有多庞大，我们只要增加机器就可以了，比如单台机器特定时间能处理n个用户，2台机器处理2n个用户，3台3n，4台4n...，再多的用户也不怕了。
使用elastic-job开发的作业都是zookeeper的客户端，比如我希望3台机器跑job，我们将任务分成3片，框架通过zk的协调，最终会让3台机器分别分配到0,1,2的任务片，比如server0-->0，server1-->1，server2-->2，当server0执行时，可以只查询id%3==0的用户，server1执行时，只查询id%3==1的用户，server2执行时，只查询id%3==2的用户。
任务部署多节点引发重复执行
在上面的基础上，我们再增加server3，此时，server3分不到任务分片，因为只有3片，已经分完了。没有分到任务分片的作业程序将不执行。
如果此时server2挂了，那么server2的分片项会分配给server3，server3有了分片，就会替代server2执行。
如果此时server3也挂了，只剩下server0和server1了，框架也会自动把server3的分片随机分配给server0或者server1，可能会这样，server0-->0，server1-->1,2。
这种特性称之为弹性扩容，即elastic-job名称的由来。
代码演示
我们搭建环境通过示例代码来演示上面的例子，elastic-job是不支持单机多实例的，通过zk的协调分片是以ip为单元的。很多同学上来可能就是通过单机多实例来学习，结果导致分片和预期不一致。这里没办法，只能通过多机器或者虚拟机，我们这里使用虚拟机，另外，由于资源有限，我们这里仅仅只模拟两台机器。

节点说明：
本地宿主机器
zookeeper、job
192.168.241.1

虚拟机
job
192.168.241.128

环境说明：
Java
请使用JDK1.7及其以上版本。
Zookeeper
请使用Zookeeper3.4.6及其以上版本
Elastic-Job-Lite
2.0.5（2.x系列即可，最好是2.0.4及其以上，因为2.0.4版本有本人提交的少许代码，(*^__^*) 嘻嘻……）

需求说明：
通过两台机器演示动态分片

step1. 引入框架的jar包
<!-- 引入elastic-job-lite核心模块 -->
<dependency>
    <groupId>com.dangdang</groupId>
    <artifactId>elastic-job-lite-core</artifactId>
    <version>2.0.5</version>
</dependency>
<!-- 使用springframework自定义命名空间时引入 -->
<dependency>
    <groupId>com.dangdang</groupId>
    <artifactId>elastic-job-lite-spring</artifactId>
    <version>2.0.5</version>
</dependency>
step2. 编写job
package com.fanfan.sample001;
 
import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
 
import java.util.Date;
 
/**
 * Created by fanfan on 2016/12/20.
 */
public class MySimpleJob implements SimpleJob {
    @Override
    public void execute(ShardingContext shardingContext) {
        System.out.println(String.format("------Thread ID: %s, 任务总片数: %s, 当前分片项: %s",
                Thread.currentThread().getId(), shardingContext.getShardingTotalCount(), shardingContext.getShardingItem()));
        /**
         * 实际开发中，有了任务总片数和当前分片项，就可以对任务进行分片执行了
         * 比如 SELECT * FROM user WHERE status = 0 AND MOD(id, shardingTotalCount) = shardingItem
         */
    }
}
Step3. Spring配置
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:reg="http://www.dangdang.com/schema/ddframe/reg"
       xmlns:job="http://www.dangdang.com/schema/ddframe/job"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
                        http://www.springframework.org/schema/beans/spring-beans.xsd
                        http://www.dangdang.com/schema/ddframe/reg
                        http://www.dangdang.com/schema/ddframe/reg/reg.xsd
                        http://www.dangdang.com/schema/ddframe/job
                        http://www.dangdang.com/schema/ddframe/job/job.xsd">
    <!--配置作业注册中心 -->
    <reg:zookeeper id="regCenter" server-lists="192.168.241.1:2181" namespace="dd-job"
                   base-sleep-time-milliseconds="1000" max-sleep-time-milliseconds="3000" max-retries="3" />
 
    <!-- 配置作业-->
    <job:simple id="mySimpleJob" class="com.fanfan.sample001.MySimpleJob" registry-center-ref="regCenter"
                sharding-total-count="2" cron="0/2 * * * * ?" overwrite="true" />
 
</beans>

Case1. 单节点









Case2. 增加一个节点













Case3. 断开一个节点






作业类型
elastic-job提供了三种类型的作业：Simple类型作业、Dataflow类型作业、Script类型作业。这里主要讲解前两者。Script类型作业意为脚本类型作业，支持shell，python，perl等所有类型脚本，使用不多，可以参见github文档。

SimpleJob需要实现SimpleJob接口，意为简单实现，未经过任何封装，与quartz原生接口相似，比如示例代码中所使用的job。

Dataflow类型用于处理数据流，需实现DataflowJob接口。该接口提供2个方法可供覆盖，分别用于抓取(fetchData)和处理(processData)数据。
可通过DataflowJobConfiguration配置是否流式处理。
流式处理数据只有fetchData方法的返回值为null或集合长度为空时，作业才停止抓取，否则作业将一直运行下去； 非流式处理数据则只会在每次作业执行过程中执行一次fetchData方法和processData方法，随即完成本次作业。
实际开发中，Dataflow类型的job还是很有好用的。

比如拿余额宝计息来说：

package com.fanfan.sample001;
 
import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.dataflow.DataflowJob;
 
import java.util.ArrayList;
import java.util.List;
 
/**
 * Created by fanfan on 2016/12/23.
 */
public class MyDataFlowJob implements DataflowJob<User> {
 
    /*
        status
        0：待处理
        1：已处理
     */
 
    @Override
    public List<User> fetchData(ShardingContext shardingContext) {
        List<User> users = null;
        /**
         * users = SELECT * FROM user WHERE status = 0 AND MOD(id, shardingTotalCount) = shardingItem Limit 0, 30
         */
        return users;
    }
 
    @Override
    public void processData(ShardingContext shardingContext, List<User> data) {
        for (User user: data) {
            System.out.println(String.format("用户 %s 开始计息", user.getUserId()));
            user.setStatus(1);
            /**
             * update user
             */
        }
    }
}

<job:dataflow id="myDataFlowJob" class="com.fanfan.sample001.MyDataFlowJob" registry-center-ref="regCenter"
              sharding-total-count="2" cron="0 0 02 * * ?" streaming-process="true" overwrite="true" />

--------------------- 
作者：秋名车手 
来源：CSDN 
原文：https://blog.csdn.net/fanfan_v5/article/details/61310045 
版权声明：本文为博主原创文章，转载请附上博文链接！