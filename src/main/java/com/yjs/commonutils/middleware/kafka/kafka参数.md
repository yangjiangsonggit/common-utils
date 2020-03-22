kafka消费者消费者参数设置及参数调优建议-kafka商业环境实战


kafka商业环境实战-kafka生产环境规划
kafka商业环境实战-kafka生产者和消费者爆炸测试
kafka商业环境实战-kafka生产者生产者参数设置及参数调优建议
kafka商业环境实战-kafka纳入管理重要操作指令运维兵书
kafka商业环境实战-kafka合并经纪人端参数设置及调优指南建议
kafka商业环境实战-kafka之生产者同步与初始化消息发送及事务幂等性案例应用实战
kafka商业环境实战-kafka之消费者多种消费模式案例应用实战
kafka商业环境实战-kafka消费者消费者参数设置及参数调优建议
[kafka商业环境实战-kafka位移提交机制与消费者组的重平衡策略]
[kafka商业环境实战-kafka消息民意调查机制原理深度剖析]
[kafka商业环境实战-kafka副本与ISR同步机制原理深入剖析]
[kafka商业环境实战-kafka精确一次语义EOS的原理深入剖析]
[kafka商业环境实战-kafka消息的幂等性与事务支持机制深入剖析]
[kafka商业环境实战-kafka合并控制器预期与责任设计思路架构详解]
[kafka商业环境实战-kafka合并消息格式之音V1版本到V2版本的平滑过渡详解]
[kafka商业环境实战-kafka合并水印与leader epoch对数据一致性保障的深入研究]
[kafka商业环境实战-kafka调优过程在腐败，持久性，低延迟，可用性等指标的折中选择研究]
1消息的接收->基于消费者组
消费者组主要用于实现高伸缩性，高容错性的消费者机制。因此，消息的接收是基于消费者组的。组内部多个消费者实例可以同时读取Kafka消息，同一时刻一条消息只能被一个消费者消费，而且一旦有人某消费者“挂了”，消费者小组会立即将已经崩溃的消费者负责的分区转换交给其他消费者来负责。从而保证消费者小组能够正常工作。

2位移保存->基于消费者组
说来奇怪，位移保存是基于消费群体，同时约会检查点模式，定期实现offset的持久化。

3位移提交->抛弃ZooKeeper
消费者会定期向kafka转移汇报自己的消费数据的进度，此过程称为位移的提交。这一过程已经抛弃Zookeeper，因为Zookeeper只是一个协调服务组件，不能作为存储组件，高并发的读取势必造成Zk的压力。

新版本位移提交是在kafka内部维护了一个内部Topic（_consumer_offsets）。
在kafka内部日志目录下面，共有50个文件夹，每一个文件夹包含日志文件和索引文件。日志文件主要是KV结构，（group.id，topic，分区号）。
假设网上有很多的consumer和ConsumerGroup，通过对group.id做Hash求模运算，这50个文件夹就可以分散同时移位提交的压力。
4官方案例
4.1自动提交位移
     Properties props = new Properties();
     props.put("bootstrap.servers", "localhost:9092");
     props.put("group.id", "test");
     props.put("enable.auto.commit", "true");
     props.put("auto.commit.interval.ms", "1000");
     props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
     props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
     KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
     consumer.subscribe(Arrays.asList("foo", "bar"));
     while (true) {
         ConsumerRecords<String, String> records = consumer.poll(100);
         for (ConsumerRecord<String, String> record : records)
             System.out.printf("offset = %d, key = %s, value = %s%n", record.offset(), record.key(), record.value());
     }
复制代码
4.2手动提交位移
     Properties props = new Properties();
     props.put("bootstrap.servers", "localhost:9092");
     props.put("group.id", "test");
     props.put("enable.auto.commit", "false");
     props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
     props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
     KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
     consumer.subscribe(Arrays.asList("foo", "bar"));
     final int minBatchSize = 200;
     List<ConsumerRecord<String, String>> buffer = new ArrayList<>();
     while (true) {
         ConsumerRecords<String, String> records = consumer.poll(100);
         for (ConsumerRecord<String, String> record : records) {
             buffer.add(record);
         }
         if (buffer.size() >= minBatchSize) {
             insertIntoDb(buffer);
             consumer.commitSync();
             buffer.clear();
         }
     }
复制代码

5 kafka Consumer参数设置
Consumer.poll（1000）重要参数
新版本的消费者的投票方法使用了一次选择I / O机制，因此所有相关事件（包括reblance，消息获取等）都发生在一个事件循环之中。
1000是一个超时时间，一旦拿到足够多的数据（参数设置），consumer.poll（1000）会立即返回ConsumerRecords <String，String>记录。
如果没有拿到足够多的数据，会延长1000ms，但不会超过1000ms就会返回。
会议。暂停。ms <=协调器检测失败的时间
默认值是10s
该参数是Consumer Group主动检测（组内部成员comsummer）崩溃的时间间隔。若设置10min，那么Consumer Group的管理者（group coordinator）可能需要10分钟才能做到。太漫长了是吧。
最高 轮询。间隔。ms <=处理逻辑最大时间
这个参数是0.10.1.0版本后添加的，可能很多地方看不到喔。这个参数需要根据实际业务处理时间进行设置，一旦Consumer处理不过来，就会被踢出Consumer Group。
注意：如果业务平均处理逻辑为1分钟，那么max。轮询。间隔。ms需要设置稍微大于1分钟即可，但是会话。暂停。ms可以设置小一点（如10s），用于快速检测消费者崩溃。
自动偏移重置
该属性指定了消费者在读取一个没有替换量交替的量无效（消费者重复重置当前的转换量已经过时并且被删除了）的分区的情况下，应该作何处理，替换值是最新的，也就是从最新记录读取数据（消费者启动之后生成的记录），另一个值是最早的，意思是在转换量无效的情况下，消费者从起始位置开始读取数据。
enable.auto.commit
对于精确到一次的语义，最好手动提交移位
最大提取字节
单次获取数据的最大消息数。
最大轮询记录<=爆炸
单次投票调用返回的最大消息数，如果处理逻辑很轻量，可以适当提高该值。
一次从kafka中poll出来的数据条数，max.poll.records条数据需要在session.timeout.ms这个时间处理完
默认等级500
心跳。间隔。ms <=居然拖家带口
heartbeat心跳主要用于沟通交流，及时返回请求响应。这个时间间隔真是越快越好。因为一旦出现重新平衡，那么就会将新的分配方案或通知重新加入组的命令放进心跳响应中。
连接。最高 闲。ms <=套接字连接
kafka会定期关闭的套接字连接。至少是9分钟。如果不在乎这些资源消耗，推荐把这些参数变量-1，即不关闭这些替换连接。
请求。暂停。女士
这个配置控制一次请求响应的最大等待时间。如果在超时时间内未得到响应，kafka就会重发这条消息，或者超过重试次数的情况下直接放置为失败。
消息发送的最长等待时间。需大于session.timeout.ms这个时间
提取最小字节
服务器发送到消费端的最小数据，若是不满足这个数值重复等待直到满足指定大小。而是为1表示立即接收。
fetch.wait.max.ms
若是不满足fetch.min.bytes时，等待消费端请求的最长等待时间

0.11新功能
空消费组暂时重新平衡，主要在server.properties文件配置
group.initial.rebalance.delay.ms <=牛逼了，我的kafka，防止成员加入请求后本应立即开启的rebalance
对于用户而言，这个改进最直接的效果就是补充了一个经纪人配置：group.initial.rebalance.delay.ms，
默认是3秒钟。
在实际使用时，假设您预测你的所有消费者组成员加入需要在10秒钟内完成，那么您就可以设置该该主要角色是让协调员扩大空消费组接收到成员加入请求后本应立即开启的重新平衡。参数= 10000。
6线上采坑
org.apache.kafka.clients.consumer.CommitFailedException:
 Commit cannot be completed since the group has already rebalanced and assigned the partitions to another member. 
This means that the time between subsequent calls to poll() was longer than the configured session.timeout.ms, which typically implies that the poll loop is spending too much time message processing. 
You can address this either by increasing the session timeout or by reducing the maximum size of batches returned in poll() with max.poll.records. [com.bonc.framework.server.kafka.consumer.ConsumerLoop]
复制代码
根据最新版本10，请注意此版本会话。暂停。ms与max.poll.interval.ms进行功能分离了。
可以发现更多reblance，并伴随者重复性消费，这是一个很严重的问题，就是处理逻辑过重，max.poll。interval.ms过小导致。发生的原因就是poll（）的循环调用时间过长，出现了处理超时。此时只用调大max.poll。interval.ms，调小最大.poll.records立即，同时要把请求。暂停。ms设置大于max.poll。间隔
7总结
优化会继续，暂时把核心放在要求上。暂停。毫秒，最大值 轮询。间隔。ms，max.poll.records上，避免因为处理逻辑过重，导致消费者被替代的踢出消费者群组。