Kafka核心配置参数与机制

1. kafka集群安装

kafka集群安装，参考文章: Kafka集群搭建及生产者消费者案例



2. kafka配置参数


broker.id：broker的id，id是唯一的非负整数，集群的broker.id不能重复。
log.dirs：kafka存放数据的路径。可以是多个，多个使用逗号分隔即可。
port：server接受客户端连接的端口，默认6667
zookeeper.connect：zookeeper集群连接地址。

格式如：zookeeper.connect=server01:2181,server02:2181,server03:2181。

如果需要指定zookeeper集群的路径位置，可以：zookeeper.connect=server01:2181,server02:2181,server03:2181/kafka/cluster。这样设置后，在启动kafka集群前，需要在zookeeper集群创建这个路径/kafka/cluster。
message.max.bytes：server可以接受的消息最大尺寸。默认1000000。

重要的是，consumer和producer有关这个属性的设置必须同步，否则producer发布的消息对consumer来说太大。
num.network.threads：server用来处理网络请求的线程数，默认3。
num.io.threads：server用来处理请求的I/O线程数。这个线程数至少等于磁盘的个数。
background.threads：用于后台处理的线程数。例如文件的删除。默认4。
queued.max.requests：在网络线程停止读取新请求之前，可以排队等待I/O线程处理的最大请求个数。默认500。
host.name：broker的hostname

如果hostname已经设置的话，broker将只会绑定到这个地址上；如果没有设置，它将绑定到所有接口，并发布一份到ZK
advertised.host.name：如果设置，则就作为broker 的hostname发往producer、consumers以及其他brokers
advertised.port：此端口将给与producers、consumers、以及其他brokers，它会在建立连接时用到； 它仅在实际端口和server需要绑定的端口不一样时才需要设置。
socket.send.buffer.bytes：SO_SNDBUFF 缓存大小，server进行socket 连接所用，默认100*1024。
socket.receive.buffer.bytes：SO_RCVBUFF缓存大小，server进行socket连接时所用。默认100 * 1024。
socket.request.max.bytes：server允许的最大请求尺寸；这将避免server溢出，它应该小于Java heap size。
num.partitions：如果创建topic时没有给出划分partitions个数，这个数字将是topic下partitions数目的默认数值。默认1。
log.segment.bytes：topic partition的日志存放在某个目录下诸多文件中，这些文件将partition的日志切分成一段一段的；这个属性就是每个文件的最大尺寸；当尺寸达到这个数值时，就会创建新文件。此设置可以由每个topic基础设置时进行覆盖。默认1014*1024*1024
log.roll.hours：即使文件没有到达log.segment.bytes，只要文件创建时间到达此属性，就会创建新文件。这个设置也可以有topic层面的设置进行覆盖。默认24*7
log.cleanup.policy：log清除策略。默认delete。
log.retention.minutes和log.retention.hours：每个日志文件删除之前保存的时间。默认数据保存时间对所有topic都一样。

log.retention.minutes 和 log.retention.bytes 都是用来设置删除日志文件的，无论哪个属性已经溢出。

这个属性设置可以在topic基本设置时进行覆盖。
log.retention.bytes：每个topic下每个partition保存数据的总量。

注意，这是每个partitions的上限，因此这个数值乘以partitions的个数就是每个topic保存的数据总量。如果log.retention.hours和log.retention.bytes都设置了，则超过了任何一个限制都会造成删除一个段文件。

注意，这项设置可以由每个topic设置时进行覆盖。
log.retention.check.interval.ms：检查日志分段文件的间隔时间，以确定是否文件属性是否到达删除要求。默认5min。
log.cleaner.enable：当这个属性设置为false时，一旦日志的保存时间或者大小达到上限时，就会被删除；如果设置为true，则当保存属性达到上限时，就会进行log compaction。默认false。
log.cleaner.threads：进行日志压缩的线程数。默认1。
log.cleaner.io.max.bytes.per.second：进行log compaction时，log cleaner可以拥有的最大I/O数目。这项设置限制了cleaner，以避免干扰活动的请求服务。
log.cleaner.io.buffer.size：log cleaner清除过程中针对日志进行索引化以及精简化所用到的缓存大小。最好设置大点，以提供充足的内存。默认500*1024*1024。
log.cleaner.io.buffer.load.factor：进行log cleaning时所需要的I/O chunk尺寸。你不需要更改这项设置。默认512*1024。
log.cleaner.io.buffer.load.factor：log cleaning中所使用的hash表的负载因子；你不需要更改这个选项。默认0.9
log.cleaner.backoff.ms：进行日志是否清理检查的时间间隔，默认15000。
log.cleaner.min.cleanable.ratio：这项配置控制log compactor试图清理日志的频率（假定log compaction是打开的）。

默认避免清理压缩超过50%的日志。这个比率绑定了备份日志所消耗的最大空间（50%的日志备份时压缩率为50%）。更高的比率则意味着浪费消耗更少，也就可以更有效的清理更多的空间。这项设置在每个topic设置中可以覆盖。
log.cleaner.delete.retention.ms：保存时间；保存压缩日志的最长时间；也是客户端消费消息的最长时间，与log.retention.minutes的区别在于一个控制未压缩数据，一个控制压缩后的数据；会被topic创建时的指定时间覆盖。
log.index.size.max.bytes：每个log segment的最大尺寸。注意，如果log尺寸达到这个数值，即使尺寸没有超过log.segment.bytes限制，也需要产生新的log segment。默认10*1024*1024。
log.index.interval.bytes：当执行一次fetch后，需要一定的空间扫描最近的offset，设置的越大越好，一般使用默认值就可以。默认4096。
log.flush.interval.messages：log文件“sync”到磁盘之前累积的消息条数。

因为磁盘IO操作是一个慢操作，但又是一个“数据可靠性”的必要手段，所以检查是否需要固化到硬盘的时间间隔。需要在“数据可靠性”与“性能”之间做必要的权衡，如果此值过大，将会导致每次“发sync”的时间过长（IO阻塞），如果此值过小，将会导致“fsync”的时间较长（IO阻塞），导致”发sync“的次数较多，这也就意味着整体的client请求有一定的延迟，物理server故障，将会导致没有fsync的消息丢失。
log.flush.scheduler.interval.ms：检查是否需要fsync的时间间隔。默认Long.MaxValue
log.flush.interval.ms：仅仅通过interval来控制消息的磁盘写入时机，是不足的，这个数用来控制”fsync“的时间间隔，如果消息量始终没有达到固化到磁盘的消息数，但是离上次磁盘同步的时间间隔达到阈值，也将触发磁盘同步。
log.delete.delay.ms：文件在索引中清除后的保留时间，一般不需要修改。默认60000。
auto.create.topics.enable：是否允许自动创建topic。如果是true，则produce或者fetch 不存在的topic时，会自动创建这个topic。否则需要使用命令行创建topic。默认true。
controller.socket.timeout.ms：partition管理控制器进行备份时，socket的超时时间。默认30000。
controller.message.queue.size：controller-to-broker-channles的buffer尺寸，默认Int.MaxValue。
default.replication.factor：默认备份份数，仅指自动创建的topics。默认1。
replica.lag.time.max.ms：如果一个follower在这个时间内没有发送fetch请求，leader将从ISR重移除这个follower，并认为这个follower已经挂了，默认10000。
replica.lag.max.messages：如果一个replica没有备份的条数超过这个数值，则leader将移除这个follower，并认为这个follower已经挂了，默认4000。
replica.socket.timeout.ms：leader 备份数据时的socket网络请求的超时时间，默认30*1000
replica.socket.receive.buffer.bytes：备份时向leader发送网络请求时的socket receive buffer。默认64*1024。
replica.fetch.max.bytes：备份时每次fetch的最大值。默认1024*1024。
replica.fetch.max.bytes：leader发出备份请求时，数据到达leader的最长等待时间。默认500。
replica.fetch.min.bytes：备份时每次fetch之后回应的最小尺寸。默认1。
num.replica.fetchers：从leader备份数据的线程数。默认1。
replica.high.watermark.checkpoint.interval.ms：每个replica检查是否将最高水位进行固化的频率。默认5000.
fetch.purgatory.purge.interval.requests：fetch 请求清除时的清除间隔，默认1000
producer.purgatory.purge.interval.requests：producer请求清除时的清除间隔，默认1000
zookeeper.session.timeout.ms：zookeeper会话超时时间。默认6000
zookeeper.connection.timeout.ms：客户端等待和zookeeper建立连接的最大时间。默认6000
zookeeper.sync.time.ms：zk follower落后于zk leader的最长时间。默认2000
controlled.shutdown.enable：是否能够控制broker的关闭。如果能够，broker将可以移动所有leaders到其他的broker上，在关闭之前。这减少了不可用性在关机过程中。默认true。
controlled.shutdown.max.retries：在执行不彻底的关机之前，可以成功执行关机的命令数。默认3.
controlled.shutdown.retry.backoff.ms：在关机之间的backoff时间。默认5000
auto.leader.rebalance.enable：如果这是true，控制者将会自动平衡brokers对于partitions的leadership。默认true。
leader.imbalance.per.broker.percentage：每个broker所允许的leader最大不平衡比率，默认10。
leader.imbalance.check.interval.seconds：检查leader不平衡的频率，默认300
offset.metadata.max.bytes：允许客户端保存他们offsets的最大个数。默认4096
max.connections.per.ip：每个ip地址上每个broker可以被连接的最大数目。默认Int.MaxValue。
max.connections.per.ip.overrides：每个ip或者hostname默认的连接的最大覆盖。
connections.max.idle.ms：空连接的超时限制，默认600000
log.roll.jitter.{ms,hours}：从logRollTimeMillis抽离的jitter最大数目。默认0
num.recovery.threads.per.data.dir：每个数据目录用来日志恢复的线程数目。默认1。
unclean.leader.election.enable：指明了是否能够使不在ISR中replicas设置用来作为leader。默认true
delete.topic.enable：能够删除topic，默认false。
offsets.topic.num.partitions：默认50。

由于部署后更改不受支持，因此建议使用更高的设置来进行生产（例如100-200）。
offsets.topic.retention.minutes：存在时间超过这个时间限制的offsets都将被标记为待删除。默认1440。
offsets.retention.check.interval.ms：offset管理器检查陈旧offsets的频率。默认600000。
offsets.topic.replication.factor：topic的offset的备份份数。建议设置更高的数字保证更高的可用性。默认3
offset.topic.segment.bytes：offsets topic的segment尺寸。默认104857600
offsets.load.buffer.size：这项设置与批量尺寸相关，当从offsets segment中读取时使用。默认5242880
offsets.commit.required.acks：在offset commit可以接受之前，需要设置确认的数目，一般不需要更改。默认-1。






3. kafka生产者配置参数


boostrap.servers：用于建立与kafka集群连接的host/port组。

数据将会在所有servers上均衡加载，不管哪些server是指定用于bootstrapping。

这个列表格式：host1:port1,host2:port2,…
acks：此配置实际上代表了数据备份的可用性。

acks=0： 设置为0表示producer不需要等待任何确认收到的信息。副本将立即加到socket buffer并认为已经发送。没有任何保障可以保证此种情况下server已经成功接收数据，同时重试配置不会发生作用

acks=1： 这意味着至少要等待leader已经成功将数据写入本地log，但是并没有等待所有follower是否成功写入。这种情况下，如果follower没有成功备份数据，而此时leader又挂掉，则消息会丢失。

acks=all： 这意味着leader需要等待所有备份都成功写入日志，这种策略会保证只要有一个备份存活就不会丢失数据。这是最强的保证。
buffer.memory：producer可以用来缓存数据的内存大小。如果数据产生速度大于向broker发送的速度，producer会阻塞或者抛出异常，以“block.on.buffer.full”来表明。
compression.type：producer用于压缩数据的压缩类型。默认是无压缩。正确的选项值是none、gzip、snappy。压缩最好用于批量处理，批量处理消息越多，压缩性能越好。
retries：设置大于0的值将使客户端重新发送任何数据，一旦这些数据发送失败。注意，这些重试与客户端接收到发送错误时的重试没有什么不同。

允许重试将潜在的改变数据的顺序，如果这两个消息记录都是发送到同一个partition，则第一个消息失败第二个发送成功，则第二条消息会比第一条消息出现要早。
batch.size：producer将试图批处理消息记录，以减少请求次数。这将改善client与server之间的性能。这项配置控制默认的批量处理消息字节数。
client.id：当向server发出请求时，这个字符串会发送给server。目的是能够追踪请求源头，以此来允许ip/port许可列表之外的一些应用可以发送信息。这项应用可以设置任意字符串，因为没有任何功能性的目的，除了记录和跟踪。
linger.ms：producer组将会汇总任何在请求与发送之间到达的消息记录一个单独批量的请求。通常来说，这只有在记录产生速度大于发送速度的时候才能发生。
max.request.size：请求的最大字节数。这也是对最大记录尺寸的有效覆盖。注意：server具有自己对消息记录尺寸的覆盖，这些尺寸和这个设置不同。此项设置将会限制producer每次批量发送请求的数目，以防发出巨量的请求。
receive.buffer.bytes：TCP receive缓存大小，当阅读数据时使用。
send.buffer.bytes：TCP send缓存大小，当发送数据时使用。
timeout.ms：此配置选项控制server等待来自followers的确认的最大时间。如果确认的请求数目在此时间内没有实现，则会返回一个错误。这个超时限制是以server端度量的，没有包含请求的网络延迟。
block.on.buffer.full：当我们内存缓存用尽时，必须停止接收新消息记录或者抛出错误。

默认情况下，这个设置为真，然而某些阻塞可能不值得期待，因此立即抛出错误更好。设置为false则会这样：producer会抛出一个异常错误：BufferExhaustedException， 如果记录已经发送同时缓存已满。
metadata.fetch.timeout.ms：是指我们所获取的一些元素据的第一个时间数据。元素据包含：topic，host，partitions。此项配置是指当等待元素据fetch成功完成所需要的时间，否则会抛出异常给客户端。
metadata.max.age.ms：以微秒为单位的时间，是在我们强制更新metadata的时间间隔。即使我们没有看到任何partition leadership改变。
metric.reporters：类的列表，用于衡量指标。实现MetricReporter接口，将允许增加一些类，这些类在新的衡量指标产生时就会改变。JmxReporter总会包含用于注册JMX统计
metrics.num.samples：用于维护metrics的样本数。
metrics.sample.window.ms：metrics系统维护可配置的样本数量，在一个可修正的window size。这项配置配置了窗口大小，例如。我们可能在30s的期间维护两个样本。当一个窗口推出后，我们会擦除并重写最老的窗口。
recoonect.backoff.ms：连接失败时，当我们重新连接时的等待时间。这避免了客户端反复重连。
retry.backoff.ms：在试图重试失败的produce请求之前的等待时间。避免陷入发送-失败的死循环中。






4. kafka消费者配置参数


group.id：用来唯一标识consumer进程所在组的字符串，如果设置同样的group id，表示这些processes都是属于同一个consumer group。
zookeeper.connect：指定zookeeper的连接的字符串，格式是hostname：port, hostname：port…
consumer.id：不需要设置，一般自动产生
socket.timeout.ms：网络请求的超时限制。真实的超时限制是max.fetch.wait+socket.timeout.ms。默认3000
socket.receive.buffer.bytes：socket用于接收网络请求的缓存大小。默认64*1024。
fetch.message.max.bytes：每次fetch请求中，针对每次fetch消息的最大字节数。默认1024*1024

这些字节将会督导用于每个partition的内存中，因此，此设置将会控制consumer所使用的memory大小。

这个fetch请求尺寸必须至少和server允许的最大消息尺寸相等，否则，producer可能发送的消息尺寸大于consumer所能消耗的尺寸。
num.consumer.fetchers：用于fetch数据的fetcher线程数。默认1
auto.commit.enable：如果为真，consumer所fetch的消息的offset将会自动的同步到zookeeper。这项提交的offset将在进程挂掉时，由新的consumer使用。默认true。
auto.commit.interval.ms：consumer向zookeeper提交offset的频率，单位是秒。默认60*1000。
queued.max.message.chunks：用于缓存消息的最大数目，每个chunk必须和fetch.message.max.bytes相同。默认2。
rebalance.max.retries：当新的consumer加入到consumer group时，consumers集合试图重新平衡分配到每个consumer的partitions数目。如果consumers集合改变了，当分配正在执行时，这个重新平衡会失败并重入。默认4
fetch.min.bytes：每次fetch请求时，server应该返回的最小字节数。如果没有足够的数据返回，请求会等待，直到足够的数据才会返回。
fetch.wait.max.ms：如果没有足够的数据能够满足fetch.min.bytes，则此项配置是指在应答fetch请求之前，server会阻塞的最大时间。默认100
rebalance.backoff.ms：在重试reblance之前backoff时间。默认2000
refresh.leader.backoff.ms：在试图确定某个partition的leader是否失去他的leader地位之前，需要等待的backoff时间。默认200
auto.offset.reset：zookeeper中没有初始化的offset时，如果offset是以下值的回应：

lastest：自动复位offset为lastest的offset

earliest：自动复位offset为earliest的offset

none：向consumer抛出异常
consumer.timeout.ms：如果没有消息可用，即使等待特定的时间之后也没有，则抛出超时异常
exclude.internal.topics：是否将内部topics的消息暴露给consumer。默认true。
paritition.assignment.strategy：选择向consumer 流分配partitions的策略，可选值：range，roundrobin。默认range。
client.id：是用户特定的字符串，用来在每次请求中帮助跟踪调用。它应该可以逻辑上确认产生这个请求的应用。
zookeeper.session.timeout.ms：zookeeper 会话的超时限制。默认6000

如果consumer在这段时间内没有向zookeeper发送心跳信息，则它会被认为挂掉了，并且reblance将会产生
zookeeper.connection.timeout.ms：客户端在建立通zookeeper连接中的最大等待时间。默认6000
zookeeper.sync.time.ms：ZK follower可以落后ZK leader的最大时间。默认1000
offsets.storage：用于存放offsets的地点： zookeeper或者kafka。默认zookeeper。
offset.channel.backoff.ms：重新连接offsets channel或者是重试失败的offset的fetch/commit请求的backoff时间。默认1000
offsets.channel.socket.timeout.ms：当读取offset的fetch/commit请求回应的socket 超时限制。此超时限制是被consumerMetadata请求用来请求offset管理。默认10000。
offsets.commit.max.retries：重试offset commit的次数。这个重试只应用于offset commits在shut-down之间。默认5。
dual.commit.enabled：如果使用“kafka”作为offsets.storage，你可以二次提交offset到zookeeper(还有一次是提交到kafka）。

在zookeeper-based的offset storage到kafka-based的offset storage迁移时，这是必须的。对任意给定的consumer group来说，比较安全的建议是当完成迁移之后就关闭这个选项
partition.assignment.strategy：在“range”和“roundrobin”策略之间选择一种作为分配partitions给consumer 数据流的策略。

循环的partition分配器分配所有可用的partitions以及所有可用consumer线程。它会将partition循环的分配到consumer线程上。如果所有consumer实例的订阅都是确定的，则partitions的划分是确定的分布。

循环分配策略只有在以下条件满足时才可以：（1）每个topic在每个consumer实力上都有同样数量的数据流。（2）订阅的topic的集合对于consumer group中每个consumer实例来说都是确定的






5. kafka ack容错机制(应答机制)

在Producer（生产者）向kafka集群发送消息，kafka集群会在接受完消息后，给出应答，成功或失败，如果失败，producer（生产者）会再次发送，直到成功为止。

producer（生产者）发送数据给kafka集群，kafka集群反馈有3种模式：

0：producer（生产者）不会等待kafka集群发送ack，producer(生产者)发送完消息就算成功。

1：producer(生产者)等待kafka集群的leader接受到消息后，发送ack。producer（生产者）接收到ack，表示消息发送成功。

-1：producer(生产者)等待kafka集群所有包含分区的follower都同步消息成功后，发送ack。producer(生产者)接受到ack，表示消息发送成功。



6. kafka segment

在Kafka文件存储中，同一个topic下有多个不同partition，每个partition为一个目录，partiton命名规则为topic名称+有序序号，第一个partiton序号从0开始，序号最大值为partitions数量减1。

每个partion(目录)相当于一个巨型文件被平均分配到多个大小相等segment(段)数据文件中。但每个段segment file消息数量不一定相等，这种特性方便old segment file快速被删除。默认保留7天的数据。

每个partiton只需要支持顺序读写就行了，segment文件生命周期由服务端配置参数决定。（什么时候创建，什么时候删除）

数据有序性：只有在一个partition分区内，数据才是有序的。

Segment file组成：由2大部分组成，分别为i**ndex file**和data file，此2个文件一一对应，成对出现，后缀”.index”和“.log”分别表示为segment索引文件、数据文件。（在目前最新版本，又添加了另外的约束）。

Segment文件命名规则：partion全局的第一个segment从0开始，后续每个segment文件名为上一个segment文件最后一条消息的offset值。数值最大为64位long大小，19位数字字符长度，没有数字用0填充。

索引文件存储大量元数据，数据文件存储大量消息，索引文件中元数据指向对应数据文件中message的物理偏移地址。

segment机制的作用： 
- 可以通过索引快速找到消息所在的位置。 


用于超过kafka设置的默认时间，清除比较方便。

---------------------

本文来自 张行之 的CSDN 博客 ，全文地址请点击：https://blog.csdn.net/qq_33689414/article/details/80621572?utm_source=copy 