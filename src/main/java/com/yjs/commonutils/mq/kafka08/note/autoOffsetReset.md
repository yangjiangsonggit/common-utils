Kafka auto.offset.reset值详解
2017年07月06日 11:25:22 lishuangzhe7047 阅读数：30815 标签： kafka  更多
个人分类： Kafka
版权声明：本文为博主原创文章，未经博主允许不得转载。	https://blog.csdn.net/lishuangzhe7047/article/details/74530417
昨天在写一个java消费kafka数据的实例，明明设置auto.offset.reset为earliest，但还是不从头开始消费，官网给出的含义太抽象了。 
earliest: automatically reset the offset to the earliest offset，自动将偏移量置为最早的。难道不是topic中各分区的开始？结果还真不是，具体含义如下：

auto.offset.reset值含义解释
earliest 
当各分区下有已提交的offset时，从提交的offset开始消费；无提交的offset时，从头开始消费 
latest 
当各分区下有已提交的offset时，从提交的offset开始消费；无提交的offset时，消费新产生的该分区下的数据 
none 
topic各分区都存在已提交的offset时，从offset后开始消费；只要有一个分区不存在已提交的offset，则抛出异常

以下为测试详细：

1.同分组下测试
1.1测试一
1.1.1测试环境
Topic为lsztopic7，并生产30条信息。lsztopic7详情： 
这里写图片描述
创建组为“testtopi7”的consumer，将enable.auto.commit设置为false，不提交offset。依次更改auto.offset.reset的值。此时查看offset情况为： 
这里写图片描述

1.1.2测试结果
earliest 
客户端读取30条信息，且各分区的offset从0开始消费。 
latest 
客户端读取0条信息。 
none 
抛出NoOffsetForPartitionException异常。 
这里写图片描述

1.1.3测试结论
新建一个同组名的消费者时，auto.offset.reset值含义： 
earliest 每个分区是从头开始消费的。 
none 没有为消费者组找到先前的offset值时，抛出异常

1.2测试二
1.2.1测试环境
测试场景一下latest时未接受到数据，保证该消费者在启动状态，使用生产者继续生产10条数据，总数据为40条。 
这里写图片描述

1.2.2测试结果
latest 
客户端取到了后生产的10条数据

1.2.3测试结论
当创建一个新分组的消费者时，auto.offset.reset值为latest时，表示消费新的数据（从consumer创建开始，后生产的数据），之前产生的数据不消费。

1.3测试三
1.3.1测试环境
在测试环境二，总数为40条，无消费情况下，消费一批数据。运行消费者消费程序后，取到5条数据。 
即，总数为40条，已消费5条，剩余35条。 
这里写图片描述

1.3.2测试结果
earliest 
消费35条数据，即将剩余的全部数据消费完。

latest 
消费9条数据，都是分区3的值。 
offset:0 partition:3 
offset:1 partition:3 
offset:2 partition:3 
offset:3 partition:3 
offset:4 partition:3 
offset:5 partition:3 
offset:6 partition:3 
offset:7 partition:3 
offset:8 partition:3

none 
抛出NoOffsetForPartitionException异常。 
这里写图片描述

1.3.3测试结论
earliest 当分区下有已提交的offset时，从提交的offset开始消费；无提交的offset时，从头开始消费。 
latest 当分区下有已提交的offset时，从提交的offset开始消费；无提交的offset时，消费新产生的该分区下的数据。 
none 当该topic下所有分区中存在未提交的offset时，抛出异常。

1.4测试四
1.4.1测试环境
再测试三的基础上，将数据消费完，再生产10条数据，确保每个分区上都有已提交的offset。 
此时，总数为50，已消费40，剩余10条 
这里写图片描述

1.4.2测试结果
none 
消费10条信息，且各分区都是从offset开始消费 
offset:9 partition:3 
offset:10 partition:3 
offset:11 partition:3 
offset:15 partition:0 
offset:16 partition:0 
offset:17 partition:0 
offset:18 partition:0 
offset:19 partition:0 
offset:20 partition:0 
offset:5 partition:2

1.4.3测试结论
值为none时，topic各分区都存在已提交的offset时，从offset后开始消费；只要有一个分区不存在已提交的offset，则抛出异常。

2.不同分组下测试
2.1测试五
2.1.1测试环境
在测试四环境的基础上：总数为50，已消费40，剩余10条，创建不同组的消费者，组名为testother7 
这里写图片描述

2.1.2 测试结果
earliest 
消费50条数据，即将全部数据消费完。

latest 
消费0条数据。

none 
抛出异常 
这里写图片描述

2.1.3测试结论
组与组间的消费者是没有关系的。 
topic中已有分组消费数据，新建其他分组ID的消费者时，之前分组提交的offset对新建的分组消费不起作用。