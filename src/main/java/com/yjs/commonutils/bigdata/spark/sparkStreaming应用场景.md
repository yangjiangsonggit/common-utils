##Spark Streaming 场景应用


    Spark Streaming 是一套优秀的实时计算框架。其良好的可扩展性、高吞吐量以及容错机制能够满足我们很多的场景应用。本篇结合我们的应用场景，
    介结我们在使用 Spark Streaming 方面的技术架构，并着重讲解 Spark Streaming 两种计算模型，无状态和状态计算模型以及该两种模型的注意事项;
    接着介绍了 Spark Streaming 在监控方面所做的一些事情，最后总结了 Spark Streaming 的优缺点。
    
    一、概述
        数据是非常宝贵的资源，对各级企事业单均有非常高的价值。但是数据的爆炸，导致原先单机的数据处理已经无法满足业务的场景需求。因此在此
        基础上出现了一些优秀的分布式计算框架，诸如 Hadoop、Spark 等。离线分布式处理框架虽然能够处理非常大量的数据，但是其迟滞性很难满足
        一些特定的需求场景，比如 push 反馈、实时推荐、实时用户行为等。为了满足这些场景，使数据处理能够达到实时的响应和反馈，又随之出现了
        实时计算框架。目前的实时处理框架有 Apache Storm、Apache Flink 以及 Spark Streaming 等。其中 Spark Streaming 由于其本身的扩展性、高吞吐量以及容错能力等特性，并且能够和离线各种框架有效结合起来，因而是当下是比较受欢迎的一种流式处理框架。
        根据其官方文档介绍，Spark Streaming 有高扩展性、高吞吐量和容错能力强的特点。Spark Streaming 支持的数据输入源很多，例如：Kafka、Flume、Twitter、ZeroMQ 和简单的 TCP 套接字等等。数据输入后可以用 Spark 的高度抽象原语如：map、reduce、join、window 等进行运算。而结果也能保存在很多地方，如 HDFS，数据库等。另外 Spark Streaming 也能和 MLlib(机器学习)以及 Graphx 完美融合。其架构见下图：
        Spark Streaming 其优秀的特点给我们带来很多的应用场景，如网站监控和网络监控、异常监测、网页点击、用户行为、用户迁移等。本文中，将为大家详细介绍，我们的应用场景中，Spark Streaming 的技术架构、两种状态模型以及 Spark Streaming 监控等。
    
    二、应用场景
    
        在 Spark Streaming 中，处理数据的单位是一批而不是单条，而数据采集却是逐条进行的，因此 Spark Streaming 系统需要设置间隔使得数据汇总到一定的量后再一并操作，这个间隔就是批处理间隔。批处理间隔是 Spark Streaming 的核心概念和关键参数，它决定了 Spark Streaming 提交作业的频率和数据处理的延迟，同时也影响着数据处理的吞吐量和性能。
    
    2.1 框架
    
    目前我们 Spark Streaming 的业务应用场景包括异常监测、网页点击、用户行为以及用户地图迁徙等场景。按计算模型来看大体可分为无状态的计算模型以及状态计算模型两种。在实际的应用场景中，我们采用Kafka作为实时输入源，Spark Streaming 作为计算引擎处理完数据之后，再持久化到存储中，包括 MySQL、HDFS、ElasticSearch 以及 MongoDB 等；同时 Spark Streaming 数据清洗后也会写入 Kafka，然后经由 Flume 持久化到 HDFS；接着基于持久化的内容做一些 UI 的展现。架构见下图：
    
    2.2 无状态模型
    
    无状态模型只关注当前新生成的 DStream 数据，所以的计算逻辑均基于该批次的数据进行处理。无状态模型能够很好地适应一些应用场景，比如网站点击实时排行榜、指定 batch 时间段的用户访问以及点击情况等。该模型由于没有状态，并不需要考虑有状态的情况，只需要根据业务场景保证数据不丢就行。此种情况一般采用 Direct 方式读取 Kafka 数据，并采用监听器方式持久化 Offsets 即可。具体流程如下：
    
    其上模型框架包含以下几个处理步骤：
    
    读取 Kafka 实时数据；
    
    Spark Streaming Transformations 以及 actions 操作；
    
    将数据结果持久化到存储中，跳转到步骤一。
    
    受网络、集群等一些因素的影响，实时程序出现长时失败，导致数据出现堆积。此种情况下是丢掉堆积的数据从 Kafka largest 处消费还是从之前的 Kafka offsets处消费，这个取决具体的业务场景。
    
    2.3 状态模型
    
    有状态模型是指 DStreams 在指定的时间范围内有依赖关系，具体的时间范围由业务场景来指定，可以是 2 个及以上的多个 batch time RDD 组成。Spark Streaming 提供了 updateStateByKey 方法来满足此类的业务场景。因涉及状态的问题，所以在实际的计算过程中需要保存计算的状态，Spark Streaming 中通过 checkpoint 来保存计算的元数据以及计算的进度。该状态模型的应用场景有网站具体模块的累计访问统计、最近 N batch time 的网站访问情况以及 app 新增累计统计等等。具体流程如下：
    
    上述流程中，每 batch time 计算时，需要依赖最近 2 个 batch time 内的数据，经过转换及相关统计，最终持久化到 MySQL 中去。不过为了确保每个计算仅计算 2 个 batch time 内的数据，需要维护数据的状态，清除过期的数据。我们先来看下 updateStateByKey 的实现，其代码如下：
    
    暴露了全局状态数据中的 key 类型的方法。
    
    def updateStateByKey[S: ClassTag](
    
    updateFunc: (Iterator[(K, Seq[V], Option[S])]) => Iterator[(K, S)],
    
    partitioner: Partitioner,
    
    rememberPartitioner: Boolean
    
    ): DStream[(K, S)] = ssc.withScope {
    
    new StateDStream(self, ssc.sc.clean(updateFunc), partitioner, rememberPartitioner, None)
    
    }
    
    隐藏了全局状态数据中的 key 类型，仅对 Value 提供自定义的方法。
    
    def updateStateByKey[S: ClassTag](
    
    updateFunc: (Seq[V], Option[S]) => Option[S],
    
    partitioner: Partitioner,
    
    initialRDD: RDD[(K, S)]
    
    ): DStream[(K, S)] = ssc.withScope {
    
    val cleanedUpdateF = sparkContext.clean(updateFunc)
    
    val newUpdateFunc = (iterator: Iterator[(K, Seq[V], Option[S])]) => {
    
    iterator.flatMap(t => cleanedUpdateF(t._2, t._3).map(s => (t._1, s)))
    
    }
    
    updateStateByKey(newUpdateFunc, partitioner, true, initialRDD)
    
    }
    
    以上两种方法分别给我们提供清理过期数据的思路：
    
    泛型 K 进行过滤。K 表示全局状态数据中对应的 key，如若 K 不满足指定条件则反回 false；
    返回值过滤。第二个方法中自定义函数指定了 Option[S] 返回值，若过期数据返回 None，那么该数据将从全局状态中清除。
    
    三、Spark Streaming 监控
    
    同 Spark 一样，Spark Streaming 也提供了 Jobs、Stages、Storage、Enviorment、Executors 以及 Streaming 的监控，其中 Streaming 监控页的内容如下图：
    上图是 Spark UI 中提供一些数据监控，包括实时输入数据、Scheduling Delay、处理时间以及总延迟的相关监控数据的趋势展现。另外除了提供上述数据监控外，Spark UI 还提供了 Active Batches 以及 Completed Batches 相关信息。Active Batches 包含当前正在处理的 batch 信息以及堆积的 batch 相关信息，而 Completed Batches 刚提供每个 batch 处理的明细数据，具体包括 batch time、input size、scheduling delay、processing Time、Total Delay等，具体信息见下图：
    Spark Streaming 能够提供如此优雅的数据监控，是因在对监听器设计模式的使用。如若 Spark UI 无法满足你所需的监控需要，用户可以定制个性化监控信息。 Spark Streaming 提供了 StreamingListener 特质，通过继承此方法，就可以定制所需的监控，其代码如下：
    
    @DeveloperApi
    
    trait StreamingListener {
    
    /** Called when a receiver has been started */
    
    def onReceiverStarted(receiverStarted: StreamingListenerReceiverStarted) { }
    
    /** Called when a receiver has reported an error */
    
    def onReceiverError(receiverError: StreamingListenerReceiverError) { }
    
    /** Called when a receiver has been stopped */
    
    def onReceiverStopped(receiverStopped: StreamingListenerReceiverStopped) { }
    
    /** Called when a batch of jobs has been submitted for processing. */
    
    def onBatchSubmitted(batchSubmitted: StreamingListenerBatchSubmitted) { }
    
    /** Called when processing of a batch of jobs has started.  */
    
    def onBatchStarted(batchStarted: StreamingListenerBatchStarted) { }
    
    /** Called when processing of a batch of jobs has completed. */
    
    def onBatchCompleted(batchCompleted: StreamingListenerBatchCompleted) { }
    
    /** Called when processing of a job of a batch has started. */
    
    def onOutputOperationStarted(
    
    outputOperationStarted: StreamingListenerOutputOperationStarted) { }
    
    /** Called when processing of a job of a batch has completed. */
    
    def onOutputOperationCompleted(
    
    outputOperationCompleted: StreamingListenerOutputOperationCompleted) { }
    
    }
    
    目前，我们保存 Offsets 时，采用继承 StreamingListener 方式，此是一种应用场景。当然也可以监控实时计算程序的堆积情况，并在达到一阈值后发送报警邮件。具体监听器的定制还得依据应用场景而定。
    
    四、Spark Streaming 优缺点
    
    Spark Streaming 并非是 Storm 那样，其并非是真正的流式处理框架，而是一次处理一批次数据。也正是这种方式，能够较好地集成 Spark 其他计算模块，包括 MLlib(机器学习)、Graphx 以及 Spark SQL。这给实时计算带来很大的便利，与此带来便利的同时，也牺牲作为流式的实时性等性能。
    
    4.1 优点
    
    Spark Streaming 基于 Spark Core API，因此其能够与 Spark 中的其他模块保持良好的兼容性，为编程提供了良好的可扩展性;
    
    Spark Streaming 是粗粒度的准实时处理框架，一次读取完或异步读完之后处理数据，且其计算可基于大内存进行，因而具有较高的吞吐量;
    
    Spark Streaming 采用统一的 DAG 调度以及 RDD，因此能够利用其lineage 机制，对实时计算有很好的容错支持;
    
    Spark Streaming 的 DStream 是基于 RDD 的在流式数据处理方面的抽象，其 transformations 以及 actions 有较大的相似性，这在一定程度上降低了用户的使用门槛，在熟悉 Spark 之后，能够快速上手 Spark Streaming。
    
    4.2 缺点
    
    Spark Streaming 是准实时的数据处理框架，采用粗粒度的处理方式，当 batch time 到时才会触发计算，这并非像 Storm 那样是纯流式的数据处理方式。此种方式不可避免会出现相应的计算延迟 。
    目前来看，Spark Streaming 稳定性方面还是会存在一些问题。有时会因一些莫名的异常导致退出，这种情况下得需要自己来保证数据一致性以及失败重启功能等。
    
    四、总结
    
    本篇文章主要介绍了 Spark Streaming 在实际应用场景中的两种计算模型，包括无状态模型以及状态模型；并且重点关注了下 Spark Streaming 在监控方面所作的努力。
    
    首先本文介绍了 Spark Streaming 应用场景以及在我们的实际应用中所采取的技术架构。在此基础上，引入无状态计算模型以及有状态模型两种计算模型;接着通过监听器模式介绍 Spark UI 相关监控信息等；最后对 Spark Streaming 的优缺点进行概括。