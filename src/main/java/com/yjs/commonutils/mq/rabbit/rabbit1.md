### 准备
## 目标
了解 Spring AMQP 如何实现异步消息投递（推模式）
## 前置知识
《RabbitMQ入门_05_多线程消费同一队列》
## 相关资源
Quick Tour for the impatient：<http://docs.spring.io/spring-amqp/docs/1.7.3.RELEASE/reference/html/_reference.html#async-consumer>
Sample code：<https://github.com/gordonklg/study>，rabbitmq module
源码版本：Spring AMQP 1.7.3.RELEASE
## 测试代码
gordon.study.rabbitmq.springamqp.AsyncConsumer.java


 
### 分析
## MessageListener
org.springframework.amqp.core.MessageListener 是 Spring AMQP 异步消息投递的监听器接口，它只有一个方法 onMessage，用于处理消息队列推送来的消息。

 
MessageListener 大概对应 amqp client 中的 Consumer 类。onMessage 方法大概对应 Consumer 类的 handleDelivery 方法。

从这也可以看出，Spring AMQP 的 Message 类至少包含 consumer tag、envelope、basic properties 和 message body 等信息
 
## MessageListenerContainer
org.springframework.amqp.rabbit.listener.MessageListenerContainer 可以看作 message linstener 的容器。但这个 Container 的语义并不是指它包含多个 message listener，实际上从方法注释和实现代码可以看出，MessageListenerContainer 只包含一个 MessageListener 。那 Container 的语义是什么呢？
 
一方面，Container 是指虽然只有一个 MessageListener 指定消息消费的逻辑，但是可以生成多个线程使用相同的 MessageListener 同时消费消息。代码第19行 setConcurrentConsumers 方法就是用来指定并发消费者的数量。可以把 MessageListenerContainer 看成下图中的 Subscriber group


 
另一方面，Container 代表生命周期管理的职责。MessageListener 仅仅实现消息消费逻辑，而整个消息消费何时开始、何时结束、如何设置、状态怎样等等问题全都是由 MessageListenerContainer（及其实现类）负责的。实际上，MessageListenerContainer 继承自 SmartLifecycle 接口，该接口是 Spring 容器提供的与生命周期管理相关的接口，实现该接口的类一般情况下会由 Spring 容器负责启动与停止。由于本例没有启用 Spring 容器环境，所以代码第26行需要主动调用 start 方法，消息消费才会开始执行。
 
## 内部实现思路
我们知道，amqp client 中的 Consumer 接口实际上只定义了回调方法，我们在回调方法（主要是 handleDelivery 方法）中实现自己的业务逻辑（对消息的消费）。Consumer 接口的回调方法实际上是在一个独立线程中执行的，当我们调用 Channel 的 basicConsume 方法时，amqp client 会创建线程处理消息、创建队列缓存从 broker 推送来的消息。然而这些内部实现并没有暴露出来，导致 Spring AMQP 必须自己重新编写一套类似的实现以获得最大的灵活度。
 
按照前面的分析，我们可以想象 Spring AMQP 为了实现自己的 message listener，需要哪些组件：
MessageListenerContainer 的实现类，即 org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer。它作为整个异步消息投递的核心类存在。
因为 MessageListenerContainer 实际上管理了一个消费者线程组，因此需要相关线程类与线程调度类。Spring AMQP 中该线程类为 org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer$AsyncMessageProcessingConsumer，调度类当然就是 SimpleMessageListenerContainer，其 start 方法会启动线程
消息队列推送过来的消息需要一个本地队列缓存。
需要实现 amqp client 的 Consumer 接口。在该接口实现类中，我们简单的把消息放到本地队列中。org.springframework.amqp.rabbit.listener.BlockingQueueConsumer$InternalConsumer 负责这件事
根据单一职责原则，线程类只负责异步消费者的创建与（无限循环）消息消费；InternalConsumer 只负责实现 amqp client 的 Consumer 接口，与 amqp client 原生的异步消息投递实现对接，将消息放入本地队列。那么，我们还需要一个真正的异步消费者模型，用来管理消费行为与状态。org.springframework.amqp.rabbit.listener.BlockingQueueConsumer 承担这部分责任。从名字可以看出，BlockingQueueConsumer 采用 BlockingQueue 作为本地队列缓存消息。
用户的业务逻辑是在 MessageListener 接口中实现的，框架的主要处理过程为：创建合适的连接与信道，从 amqp client 中获取消息暂存到本地缓存，从本地缓存读取消息并调用 MessageListener 接口的 onMessage 方法消费消息。
 

## 内部流程分析
SimpleMessageListenerContainer 的 start 方法会根据 int concurrentConsumers 的值创建对应数量的 BlockingQueueConsumer 实例，并放入 Set<BlockingQueueConsumer> consumers 中。接着为每一个 BlockingQueueConsumer 创建对应的消息处理线程 AsyncMessageProcessingConsumer（实现了 Runnable 接口），并通过 Executor taskExecutor = new SimpleAsyncTaskExecutor() 这个自实现的线程池启动每一个 AsyncMessageProcessingConsumer 线程。最后通过判断每一个 AsyncMessageProcessingConsumer 的 FatalListenerStartupException startupException 属性是否有值来判断 SimpleMessageListenerContainer 是否正常启动了所有的消息监听器。
 
构建 BlockingQueueConsumer 的构造函数参数很多，其中 ConnectionFactory 是代码第17行创建的 CachingConnectionFactory，AcknowledgeMode 默认为 AUTO。
org.springframework.amqp.core.AcknowledgeMode 定义了三种确认模式：
NONE：不确认，相当于 amqp client 中 Channel.basicConsume 方法中 autoAck 参数值设为 true
MANUAL：用户通过 channel aware listener 手动控制消息确认
AUTO：Spring AMQP 框架根据 message listener 的 onMessage 执行过程中是否抛出异常来决定是否确认消息消费
 
AsyncMessageProcessingConsumer 的 run 方法比较复杂，粗略解读一下
调用 BlockingQueueConsumer 的 start 方法（不是 Runnable 接口）。
start 方法先通过 ConnectionFactoryUtils.getTransactionalResourceHolder 静态方法创建出供该线程使用的 channel，该方法返回类型是 RabbitResourceHolder。这部分代码涉及到事务，很复杂，但是本文的测试代码不涉及事务，目前只要了解多个 AsyncMessageProcessingConsumer 会生成多个 RabbitResourceHolder 实例，但是由于使用了 CachingConnectionFactory 的默认缓存模式，所以这些 RabbitResourceHolder 实例共用同一个（AMQP）连接，每个 AsyncMessageProcessingConsumer 独享该连接创建的一个（AMQP）信道即可
start 方法接着创建 InternalConsumer 实例，并调用刚创建的 AMQP 信道的 basicQos 和  basicConsume 方法开始接受消息。这样，当队列接受到消息时，amqp client 会主动调用 InternalConsumer 的 handleDelivery 方法。该方法调用 BlockingQueueConsumer.this.queue.put(new Delivery(consumerTag, envelope, properties, body)); 将消息放到 BlockingQueueConsumer 的 BlockingQueue<Delivery> queue 中。org.springframework.amqp.rabbit.support.Delivery 类封装了 amqp client 通过  handleDelivery 方法回送过来的所有参数。
有两个细节值得说一下：第一，BlockingQueueConsumer 可以同时消费多个队列，对每个队列，都会调用 basicConsume 方法让 InternalConsumer 监听当前队列（即同一个信道，同一个 Consumer ，不同的队列）；其二，可以通过 ConsumerTagStrategy tagStrategy 设定 Tag 命名规则。
接着，在 while 循环中调用 SimpleMessageListenerContainer 的 receiveAndExecute 方法，不停的尝试从 queue 中获取 Delivery 实例，将之转化为 Message，然后执行 MessageListener 的 onMessage 回调方法。
如果执行成功，则调用 AMQP 信道的 basicAck 方法确认消息消费成功。
如果执行过程中发生异常，则将异常转化为 ListenerExecutionFailedException 抛出。默认情况下，Spring AMQP 处理用户自定义异常的逻辑非常简单：调用 AMQP 信道的 basicReject 方法将消息退回队列，打印 warning 级别的日志，但不会打破 AsyncMessageProcessingConsumer 线程的 while 循环，消息消费继续进行。这部分内容下篇文章分析。
 