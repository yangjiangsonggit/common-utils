2 Redis常见问题

2.1 Redis数据类型和抽象简介



Redis不是简单的键值存储，它实际上是一个数据结构服务器，支持不同类型的值。这意味着在传统键值存储中，您将字符串键与字符串值相关联，而在Redis中，该值不仅限于简单的字符串，还可以容纳更复杂的数据结构。以下是Redis支持的所有数据结构的列表，本教程将分别进行介绍：

二进制安全字符串。
列表：根据插入顺序排序的字符串元素的集合。它们基本上是链表。
集：唯一，未排序的字符串元素的集合。
类似于Sets的排序集合，但每个字符串元素都与一个称为score的浮点值相关联。元素总是按它们的分数排序，因此与Sets不同，可以检索一系列元素（例如，您可能会问：给我前10名或后10名）。
哈希，是由与值关联的字段组成的映射。字段和值都是字符串。这与Ruby或Python哈希非常相似。
位数组（或简称为位图）：可以使用特殊命令像位数组一样处理字符串值：您可以设置和清除单个位，计数所有设置为1的位，找到第一个设置或未设置的位，等等。
HyperLogLogs：这是一个概率数据结构，用于估计集合的基数。别害怕，它比看起来更简单...请参阅本教程的HyperLogLog部分。
流：提供抽象日志数据类型的类地图项的仅追加集合。在“ Redis流简介”中对它们进行了深入 介绍。
从命令参考中掌握这些数据类型的工作方式以及使用什么来解决给定问题并不总是那么容易，因此，本文档是有关Redis数据类型及其最常见模式的速成课程。

对于所有示例，我们将使用该redis-cli实用程序（一个简单但方便的命令行实用程序）对Redis服务器发出命令。



2.2 Redis流(类似kafka)



Stream是Redis 5.0引入的一种新数据类型，它以更抽象的方式对日志数据结构进行建模，但是日志的本质仍然完好无损：像日志文件一样，通常实现为仅在追加模式下打开的文件， Redis流主要是仅追加数据结构。至少从概念上讲，由于Redis是流式传输在内存中表示的抽象数据类型，因此它们实现了更强大的操作，以克服日志文件本身的限制。

尽管数据结构本身非常简单，但Redis流却成为最复杂的Redis类型的原因在于它实现了其他非强制性功能：一组阻止操作，使消费者可以等待生产者将新数据添加到流中，此外还有一个称为“ 消费群体”的概念。

消费者群体最初是由流行的称为Kafka（TM）的消息传递系统引入的。Redis用完全不同的术语重新实现了一个类似的想法，但是目标是相同的：允许一组客户合作使用同一消息流的不同部分。

