# kafka自动提交和手动提交

Kafka提交offset机制
在kafka的消费者中，有一个非常关键的机制，那就是offset机制。它使得Kafka在消费的过程中即使挂了或者引发再均衡问题重新分配Partation，当下次重新恢复消费时仍然可以知道从哪里开始消费。它好比看一本书中的书签标记，每次通过书签标记(offset)就能快速找到该从哪里开始看(消费)。

Kafka对于offset的处理有两种提交方式：(1) 自动提交(默认的提交方式)   (2) 手动提交(可以灵活地控制offset)

(1) 自动提交偏移量:

Kafka中偏移量的自动提交是由参数enable_auto_commit和auto_commit_interval_ms控制的，当enable_auto_commit=True时，Kafka在消费的过程中会以频率为auto_commit_interval_ms向Kafka自带的topic(__consumer_offsets)进行偏移量提交，具体提交到哪个Partation是以算法：partation=hash(group_id)%50来计算的。

如：group_id=test_group_1，则partation=hash("test_group_1")%50=28

自动提交偏移量示例：

复制代码
 1 import pickle
 2 import uuid
 3 from kafka import KafkaConsumer
 4 
 5 consumer = KafkaConsumer(
 6     bootstrap_servers=['192.168.33.11:9092'],
 7     group_id="test_group_1",
 8     client_id="{}".format(str(uuid.uuid4())),
 9     max_poll_records=500,
10     enable_auto_commit=True,  # 默认为True 表示自动提交偏移量
11     auto_commit_interval_ms=100,  # 控制自动提交偏移量的频率 单位ms 默认是5000ms
12     key_deserializer=lambda k: pickle.loads(k),
13     value_deserializer=lambda v: pickle.loads(v)
14 )
15 
16 # 订阅消费round_topic这个主题
17 consumer.subscribe(topics=('round_topic',))
18 
19 try:
20     while True:
21         consumer_records_dict = consumer.poll(timeout_ms=1000)
22 
23         # consumer.assignment()可以获取每个分区的offset
24         for partition in consumer.assignment():
25             print('主题:{} 分区:{},需要从下面的offset开始消费:{}'.format(
26                 str(partition.topic),
27                 str(partition.partition),
28                 consumer.position(partition)
29             ))
30 
31         # 处理逻辑.
32         for k, record_list in consumer_records_dict.items():
33             print(k)
34             for record in record_list:
35                 print("topic = {},partition = {},offset = {},key = {},value = {}".format(
36                     record.topic, record.partition, record.offset, record.key, record.value)
37                 )
38 
39 finally:
40     # 调用close方法的时候会触发偏移量的自动提交 close默认autocommit=True
41     consumer.close()
复制代码
 返回结果：



在上述代码中，最后调用consumer.close()时候也会触发自动提交，因为它默认autocommit=True，源码如下：

复制代码
 1     def close(self, autocommit=True):
 2         """Close the consumer, waiting indefinitely for any needed cleanup.
 3 
 4         Keyword Arguments:
 5             autocommit (bool): If auto-commit is configured for this consumer,
 6                 this optional flag causes the consumer to attempt to commit any
 7                 pending consumed offsets prior to close. Default: True
 8         """
 9         if self._closed:
10             return
11         log.debug("Closing the KafkaConsumer.")
12         self._closed = True
13         self._coordinator.close(autocommit=autocommit)
14         self._metrics.close()
15         self._client.close()
16         try:
17             self.config['key_deserializer'].close()
18         except AttributeError:
19             pass
20         try:
21             self.config['value_deserializer'].close()
22         except AttributeError:
23             pass
24         log.debug("The KafkaConsumer has closed.")
复制代码
 

对于自动提交偏移量，如果auto_commit_interval_ms的值设置的过大，当消费者在自动提交偏移量之前异常退出，将导致kafka未提交偏移量，进而出现重复消费的问题，所以建议auto_commit_interval_ms的值越小越好。

 

(2) 手动提交偏移量:

鉴于Kafka自动提交offset的不灵活性和不精确性(只能是按指定频率的提交)，Kafka提供了手动提交offset策略。手动提交能对偏移量更加灵活精准地控制，以保证消息不被重复消费以及消息不被丢失。

对于手动提交offset主要有3种方式：1.同步提交  2.异步提交  3.异步+同步 组合的方式提交

 1.同步手动提交偏移量

同步模式下提交失败的时候一直尝试提交，直到遇到无法重试的情况下才会结束，同时同步方式下消费者线程在拉取消息会被阻塞，在broker对提交的请求做出响应之前，会一直阻塞直到偏移量提交操作成功或者在提交过程中发生异常，限制了消息的吞吐量。

复制代码
 1 """
 2 同步的方式10W条消息  4.58s
 3 """
 4 
 5 import pickle
 6 import uuid
 7 import time
 8 from kafka import KafkaConsumer
 9 
10 consumer = KafkaConsumer(
11     bootstrap_servers=['192.168.33.11:9092'],
12     group_id="test_group_1",
13     client_id="{}".format(str(uuid.uuid4())),
14     enable_auto_commit=False,  # 设置为手动提交偏移量.
15     key_deserializer=lambda k: pickle.loads(k),
16     value_deserializer=lambda v: pickle.loads(v)
17 )
18 
19 # 订阅消费round_topic这个主题
20 consumer.subscribe(topics=('round_topic',))
21 
22 try:
23     start_time = time.time()
24     while True:
25         consumer_records_dict = consumer.poll(timeout_ms=100)  # 在轮询中等待的毫秒数
26         print("获取下一轮")
27 
28         record_num = 0
29         for key, record_list in consumer_records_dict.items():
30             for record in record_list:
31                 record_num += 1
32         print("---->当前批次获取到的消息个数是:{}<----".format(record_num))
33         record_num = 0
34 
35         for k, record_list in consumer_records_dict.items():
36             for record in record_list:
37                 print("topic = {},partition = {},offset = {},key = {},value = {}".format(
38                     record.topic, record.partition, record.offset, record.key, record.value)
39                 )
40 
41         try:
42             # 轮询一个batch 手动提交一次
43             consumer.commit()  # 提交当前批次最新的偏移量. 会阻塞  执行完后才会下一轮poll
44             end_time = time.time()
45             time_counts = end_time - start_time
46             print(time_counts)
47         except Exception as e:
48             print('commit failed', str(e))
49 
50 finally:
51     consumer.close()  # 手动提交中close对偏移量提交没有影响
复制代码
 



从上述可以看出，每轮循一个批次，手动提交一次，只有当前批次的消息提交完成时才会触发poll来获取下一轮的消息，经测试10W条消息耗时4.58s

 2.异步手动提交偏移量+回调函数

 异步手动提交offset时，消费者线程不会阻塞，提交失败的时候也不会进行重试，并且可以配合回调函数在broker做出响应的时候记录错误信息。

 

复制代码
 1 """
 2 异步的方式手动提交偏移量(异步+回调函数的模式) 10W条消息 3.09s
 3 """
 4 
 5 import pickle
 6 import uuid
 7 import time
 8 from kafka import KafkaConsumer
 9 
10 consumer = KafkaConsumer(
11     bootstrap_servers=['192.168.33.11:9092'],
12     group_id="test_group_1",
13     client_id="{}".format(str(uuid.uuid4())),
14     enable_auto_commit=False,  # 设置为手动提交偏移量.
15     key_deserializer=lambda k: pickle.loads(k),
16     value_deserializer=lambda v: pickle.loads(v)
17 )
18 
19 # 订阅消费round_topic这个主题
20 consumer.subscribe(topics=('round_topic',))
21 
22 
23 def _on_send_response(*args, **kwargs):
24     """
25     提交偏移量涉及回调函数
26     :param args: args[0] --> {TopicPartition:OffsetAndMetadata}  args[1] --> Exception
27     :param kwargs:
28     :return:
29     """
30     if isinstance(args[1], Exception):
31         print('偏移量提交异常. {}'.format(args[1]))
32     else:
33         print('偏移量提交成功')
34 
35 
36 try:
37     start_time = time.time()
38     while True:
39         consumer_records_dict = consumer.poll(timeout_ms=10)
40 
41         record_num = 0
42         for key, record_list in consumer_records_dict.items():
43             for record in record_list:
44                 record_num += 1
45         print("当前批次获取到的消息个数是:{}".format(record_num))
46 
47         for record_list in consumer_records_dict.values():
48             for record in record_list:
49                 print("topic = {},partition = {},offset = {},key = {},value = {}".format(
50                     record.topic, record.partition, record.offset, record.key, record.value))
51 
52         # 避免频繁提交
53         if record_num != 0:
54             try:
55                 consumer.commit_async(callback=_on_send_response)
56             except Exception as e:
57                 print('commit failed', str(e))
58 
59         record_num = 0
60 
61 finally:
62     consumer.close()
复制代码


对于args参数：args[0]是一个dict，key是TopicPartition，value是OffsetAndMetadata，表示该主题下的partition对应的offset；args[1]在提交成功是True，提交失败时是一个Exception类。

对于异步提交，由于不会进行失败重试，当消费者异常关闭或者触发了再均衡前，如果偏移量还未提交就会造成偏移量丢失。

 3.异步+同步 组合的方式提交偏移量

针对异步提交偏移量丢失的问题，通过对消费者进行异步批次提交并且在关闭时同步提交的方式，这样即使上一次的异步提交失败，通过同步提交还能够进行补救，同步会一直重试，直到提交成功。

复制代码
 1 """
 2 同步和异步组合的方式提交偏移量
 3 """
 4 
 5 import pickle
 6 import uuid
 7 import time
 8 from kafka import KafkaConsumer
 9 
10 consumer = KafkaConsumer(
11     bootstrap_servers=['192.168.33.11:9092'],
12     group_id="test_group_1",
13     client_id="{}".format(str(uuid.uuid4())),
14     enable_auto_commit=False,  # 设置为手动提交偏移量.
15     key_deserializer=lambda k: pickle.loads(k),
16     value_deserializer=lambda v: pickle.loads(v)
17 )
18 
19 # 订阅消费round_topic这个主题
20 consumer.subscribe(topics=('round_topic',))
21 
22 
23 def _on_send_response(*args, **kwargs):
24     """
25     提交偏移量涉及的回调函数
26     :param args:
27     :param kwargs:
28     :return:
29     """
30     if isinstance(args[1], Exception):
31         print('偏移量提交异常. {}'.format(args[1]))
32     else:
33         print('偏移量提交成功')
34 
35 
36 try:
37     start_time = time.time()
38     while True:
39         consumer_records_dict = consumer.poll(timeout_ms=100)
40 
41         record_num = 0
42         for key, record_list in consumer_records_dict.items():
43             for record in record_list:
44                 record_num += 1
45         print("---->当前批次获取到的消息个数是:<----".format(record_num))
46         record_num = 0
47 
48         for k, record_list in consumer_records_dict.items():
49             print(k)
50             for record in record_list:
51                 print("topic = {},partition = {},offset = {},key = {},value = {}".format(
52                     record.topic, record.partition, record.offset, record.key, record.value)
53                 )
54 
55         try:
56             # 轮询一个batch 手动提交一次
57             consumer.commit_async(callback=_on_send_response)
58             end_time = time.time()
59             time_counts = end_time - start_time
60             print(time_counts)
61         except Exception as e:
62             print('commit failed', str(e))
63 
64 except Exception as e:
65     print(str(e))
66 finally:
67     try:
68         # 同步提交偏移量,在消费者异常退出的时候再次提交偏移量,确保偏移量的提交.
69         consumer.commit()
70         print("同步补救提交成功")
71     except Exception as e:
72         consumer.close()
复制代码
通过finally在最后不管是否异常都会触发consumer.commit()来同步补救一次，确保偏移量不会丢失