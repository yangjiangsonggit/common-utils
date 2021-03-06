##接口调优
    https://www.jianshu.com/p/f386fdc50620
随着移动互联网的兴起，海量的终端带来的是数据量的飞速增长，对于存储的需求也随之增长；随之引发的接口响应率等问题也不断暴露.....

业务背景
业务之初采用的技术架构：

使用单库单表进行存储app信息
接口层面与库直接交互操作
服务状态：

数据库采用NoSql中的MongoDB；
接口每日请求10亿，超时率在15%左右
数据量激增带来的问题
数据量的激增给我们的系统带来了哪些挑战？

使用单表存储导致单表数据量过大，索引数据过大；存储+索引竟达到200G
接口直接与库操作导致接口响应(请求时间>100ms)缓慢；MongoDB是基于内存的数据库，单库单表特别影响性能
推送时直接走库导致推送过慢
解决方案
为了提高接口的性能，并通过一步步的引入缓存、队列等中间件减少数据库的读请求和分离数据库的写请求，且对线程池的调优。

Version 1
此版本接入了缓存Redis(Codis集群)，通过Redis的高性能以及多样的数据结构来减少直接查询数据库的操作。

友情链接：Redis文档-中文版

注意事项：

需要考虑缓存穿透和缓存雪崩的情况；
缓存与数据库的数据一致性问题
缓存与数据库一致性保证
如何校验数据库和缓存之间数据的一致性
接入缓存后，数据库的请求减少，接口超时率平均在10%左右，在晚高峰时段还是能达10%左右

尚未解决问题：

接口内直接对库进行写操作
Mongo数据库单表数据量达到亿级，并且存在无效索引
Version 2
此版本接入了队列，通过队列将数据库插入和修改操作在队列消费端操作，在接口中隔离了对数据库的操作。

引入队列后，博主以为接口应该不会超时了，虽然数据库的数据量比较大，CRUD比较慢，可接口中已经将写操作清零，读操作减少一大半。查询nginx日志发现超时尽管有所减少，但超时每秒还是有几百，总体每日平均超时为5%左右。

这个问题让我有点想不通，于是博主便想着去看下Java线程是否可以调优，目前系统使用的是Netty4作为容器，监听端口来响应；作了以下事情：

查看GC日志，配合jstat命令查询GC频率，是否出现频繁GC，或者Full GC次数过多等情况
查看线程日志，配合jstack命令分析是否出现线程死锁，Object.wait()情况
通过jdk自带的jmc工具观察堆内存使用情况；也可观察线程占用CPU百分比
通过上面的三步操作，得到结果：

GC频率不高，Full GC几乎没有
内存使用正常，尚未超过临界值
通过jstack发现大量的线程处于WAITING状态，可是在jstack中尚未发现是哪个方法是引起WAITING的元凶
于是去网上找资料，如何能定位某个方法的执行时间，找到了jdk自带的jvisualvm工具[Notes：jdk1.7u45后才自带有该工具，否则需要自行安装]

打开jvisualvm->远程->添加JMX连接->抽样器->CPU->在CPU样例中点击快照

Notes:快照需要等一两分钟跑了数据后再进行生成，一般生成两三次快照进行观察对比。

博主通过jvisualvm的快照中发现大量的线程在自己实现的业务handler中处理时间过长，在这里友情链接下Netty的实现原理

由于业务handler线程只开启了CPU*2个，导致io线程阻塞，无法接收新的请求，超时率高。于是博主将线程数调高，超时率已经只有1%左右。

下面附上个人对Netty线程的理解：

netty是基于boss，worker，handler三者相同配合的nio框架

1. boss：负责接收io请求，实际情况下，如果只监听了一个端口，只需要开启一个boss
2. worker： 负责处理io请求，一般个数不要超过CPU核数，默认为CPU\*2，超过反而影响性能
3. handler：负责处理业务的线程，如果是在高并发环境，可以将线程数调大，但一台机器的线程数最好`不要超过1000`，否则影响性能。将线程数调大可以防止worker堵塞。
Version 3
在版本1和2中分别在业务架构上引入了缓存和队列，并进行了线程调优，缓解了大部分的接口压力，提升了接口响应时间；

此版本将就Mongo的基础进行优化。

关于Mongo的实现可参阅官方文档：MongoDB Manual

上文中提到过数据存储用Mongo，并采用单库单表的形式在存储app数据，并且由于历史原因存在无效索引；所以可采取以下方式：

删除无效索引，减少索引占用的内存
由于Mongo内存管理部分完全交由操作系统内核处理，在执行update或delete操作时容易产生内存碎片，导致运行时间过长容易造成大量内存无法被利用。所以需要定期回收Mongo空间，释放内存(具体方式请自行Google)
将数据库单库单表形式变成多库多表，进行水平拆分，
将设备与app的关系存储在按Hash取模的方式的表中，
将大表拆分，按app分表存储
下面附上MongoDB在使用过程中的一些优化建议：Mongodb 实战优化

作者：locoder
链接：https://www.jianshu.com/p/f386fdc50620
來源：简书
简书著作权归作者所有，任何形式的转载都请联系作者获得授权并注明出处。