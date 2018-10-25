###为什么Kafka中的分区数只能增加不能减少？

当一个主题被创建之后，依然允许我们对其做一定的修改，比如修改分区个数、修改配置等，这个修改的功能就是由kafka-topics.sh脚本中的alter指令所提供。我们首先来看如何增加主题的分区数。



以前面的主题topic-config为例，当前分区数为1，修改为3，示例如下：

[root@node1 kafka_2.11-2.0.0]# bin/kafka-topics.sh --zookeeper 
localhost:2181/kafka --alter --topic topic-config --partitions 3
WARNING: If partitions are increased for a topic that has a key, 
the partition logic or ordering of the messages will be affected
Adding partitions succeeded!

[root@node1 kafka_2.11-2.0.0]# bin/kafka-topics.sh --zookeeper 
localhost:2181/kafka --describe --topic topic-config
Topic:topic-config    PartitionCount:3  ReplicationFactor:1   Configs:
Topic: topic-config    Partition: 0 Leader: 2    Replicas: 2  Isr: 2
Topic: topic-config    Partition: 1 Leader: 0    Replicas: 0  Isr: 0
Topic: topic-config    Partition: 2 Leader: 1    Replicas: 1  Isr: 1


注意上面提示的告警信息：当主题中的消息包含有key时（即key不为null），根据key来计算分区的行为就会有所影响。当topic-config的分区数为1时，不管消息的key为何值，消息都会发往这一个分区中；当分区数增加到3时，那么就会根据消息的key来计算分区号，原本发往分区0的消息现在有可能会发往分区1或者分区2中。如此还会影响既定消息的顺序，所以在增加分区数时一定要三思而后行。对于基于key计算的主题而言，建议在一开始就设置好分区数量，避免以后对其进行调整。



目前Kafka只支持增加分区数而不支持减少分区数。比如我们再将主题topic-config的分区数修改为1，就会报出InvalidPartitionException的异常，示例如下：

[root@node1 kafka_2.11-2.0.0]# bin/kafka-topics.sh --zookeeper 
localhost:2181/kafka --alter --topic topic-config --partitions 1
WARNING: If partitions are increased for a topic that has a key, 
the partition logic or ordering of the messages will be affected
Error while executing topic command : The number of partitions 
for a topic can only be increased. Topic topic-config currently 
has 3 partitions, 1 would not be an increase.
[2018-09-10 19:28:40,031] ERROR 
org.apache.kafka.common.errors.InvalidPartitionsException: 
The number of partitions for a topic can only be increased. 
Topic topic-config currently has 3 partitions, 1 would not 
be an increase. (kafka.admin.TopicCommand$)


为什么不支持减少分区？



按照Kafka现有的代码逻辑而言，此功能完全可以实现，不过也会使得代码的复杂度急剧增大。实现此功能需要考虑的因素很多，比如删除掉的分区中的消息该作何处理？如果随着分区一起消失则消息的可靠性得不到保障；
如果需要保留则又需要考虑如何保留。直接存储到现有分区的尾部，消息的时间戳就不会递增，如此对于Spark、Flink这类需要消息时间戳（事件时间）的组件将会受到影响；
如果分散插入到现有的分区中，那么在消息量很大的时候，内部的数据复制会占用很大的资源，而且在复制期间，此主题的可用性又如何得到保障？与
此同时，顺序性问题、事务性问题、以及分区和副本的状态机切换问题都是不得不面对的。反观这个功能的收益点却是很低，如果真的需要实现此类的功能，
完全可以重新创建一个分区数较小的主题，然后将现有主题中的消息按照既定的逻辑复制过去即可。

虽然分区数不可以减少，但是分区对应的副本数是可以减少的，这个其实很好理解，你关闭一个副本时就相当于副本数减少了。
不过正规的做法是使用kafka-reassign-partition.sh脚本来实现，具体用法可以自行搜索。

