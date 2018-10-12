##kafka压测
【Kafka是什么】
用于日志处理的分布式发布-订阅消息系统，放在tomcat中运行。

官网：http://kafka.apache.org/

【内部架构+运作方式】

xx.jpg

2.png

【Kafka基本操作】
【启动与关闭】

nohup bin/kafka-server-start.sh config/server.properties &

bin/kafka-server-stop.sh

【自带的基准测试工具】

bin/kafka-console-producer.sh --zookeeper localhost:2181 --topic test 

bin/kafka-console-consumer.sh --zookeeper localhost:2181 --topic test --from-beginning

【Kafka适配压测策略】
【策略】

1、单独跑producer => 单独跑consumer => producer和consumer一起跑

采用逐步加压的方式

2、针对config/server.properties中的重要配置改动，进行对比压测

【工具】

1、压力工具

jmeter两个client分别模拟producer和consumer

2、监控工具

(1)监控Consumer

KafkaOffsetMonitor

官网：https://github.com/quantifind/KafkaOffsetMonitor

启动

nohup java -Xms512M -Xmx512M -Xss1024K -XX:PermSize=256m -XX:MaxPermSize=512m -cp 

KafkaOffsetMonitor-assembly-0.2.0.jar com.quantifind.kafka.offsetapp.OffsetGetterWeb --zk localhost:2181 
--port 8091 --refresh 5.minutes --retain 1.day 1>stdout.log 2>stderr.log &

访问

http://192.168.20.78:8091/

Kafka2.jpg

【名词解释】

topic：创建时topic名称

partition：分区编号

offset：表示该parition已经消费了多少条message

logSize：表示该partition已经写了多少条message

Lag：表示有多少条message没有被消费

Owner：表示消费者

Created：该partition创建时间

Last Seen：消费状态刷新最新时间

(2)监控producer（为了弥补上述工具监控producer的不足，手动监控并计算）

-message/sec

/home/jm/bigdata/kafka-logs/replication-offset-checkpoint

kafkatest_20150408_013 1 24332547

kafkatest_20150408_013 3 24337004

-日志量MB/sec

log.dirs=/home/jm/bigdata/kafka-logs（config/server.properties里指定）

du -hk --max-depth=1 /home/jm/bigdata/kafka-logs/kafkatest_20150408_013-1

du -hk --max-depth=1 /home/jm/bigdata/kafka-logs/kafkatest_20150408_013-3

【配置调优结论】
一、consumer和producer处理能力对比

1、consumer的读取能力大大高于producer的写入能力 
单线程参考值： 
consumer：1667message/ sec 
producer: 130message/ sec

2、考虑到实际情况下，consumer的个数远少于producer，因此consumer跑到400线程就没再加压了，可以作为参考consumer造成的磁盘繁忙程度是较高的，线上监控值得关注。单线程的IO-util%可以达到16.90%，但是增加consumer线程数，并不是线性增加磁盘繁忙程度的。

二、kafka重点参数对比-适配测试
1、num.io.threads默认值8 对比 线上配置20

【测试场景】

producer和consumer同时开启，producer200个线程，consumer2个线程

【测试数据记录】

                                 message/sec	     KB/sec
producer200线程-consumer-2线程-num.io.threads=8	producer:1885/s
consumer:3052/s	producer:1885K/s
consumer:3052K/s
producer200线程-consumer-2线程-num.io.threads=20	1865/s
3094/s	1865K/s
3094K/s
测试结果：读写partition的message/sec非常接近。

【结论】

配置建议：参考官方说法，至少和服务器的磁盘个数一样多。（可以大于磁盘个数）

2、num.partitions默认值2 对比 线上配置20

【测试场景】

producer和consumer同时开启，producer200个线程，consumer20个线程

【测试数据记录】

                               message/sec	KB/sec
producer200线程-consumer-20线程-num.partitions=4	718/s
208/s	1865K/s
3094K/s
producer200线程-consumer-20线程-num.partitions=20	1937/s
3788/s	1868K/s
3032K/s
测试结果：如果partition的数目 小于 consumer的数目，会限制consumer并行去读取record

【结论】

配置建议：每个topic的日志分片数目partition，更多的分片允许更大的consumption并行，但是也会导致brokers产生更多的日志。过少和过多都不好。要根据实际consumer的个数来，尽量使得partition和consumer数据相匹配。

3、message.max.bytes（服务端可接收的单条message的最大byte）默认1000000 对比 线上配置536870912

参考图表：单条record大小对于message/sec和MB/sec影响

x1.jpg x2.jpg

可以看到，单条record越小，总的message/sec越高；单条record越大，总的MB/sec越高。

【结论】

(1)单条record的大小介于100和1000Bytes之间，能够较好的兼顾records/sec和MB/sec。当然如果单独追求其中一方面的数据，可以适当舍弃另一方面的考虑。

(2)配置建议：取决于实际读取单条日志record的大小设置，和producer和consumer的配置，二者要互相匹配。如果设置过大，可能consumer无法正常读取，线上的配置有点大了。

4、request.required.acks = 0 1 -1三个值的取舍

0 => producer从不等待broker的回复。 => 提供最小的延迟，但是容灾性最差（服务器挂了可能导致部分数据丢失）

1 => producer等待leader replica成功收到数据的回复（不等待其他replica的回复）。 => 提供比0选项更好的容灾性（如果leader replica挂了，而且还没来得及复制到其他replica上，可能导致部分数据丢失）

-1 => producer等待回复，直到所有的replica收到了数据。 => 提供最好的容灾性（只要有一个replica不挂，数据就不会丢失）

【对比测试数据】

                                message/sec	KB/sec
producer-200线程- asynchronous replication(0)	2106/s	2164K/s
producer-200线程- asynchronous replication(1)	2024/s	2080K/s
producer-200线程- synchronous replication(-1)	1794/s	1850K/s
可以看到，参数1比0，message/sec没有多少损失；但是参数-1，损失的较多。

【结论】

在数据的容灾性和性能之间做取舍，较好的参数为1，即适中的方案--- producer只等待leader副本的确认回复。