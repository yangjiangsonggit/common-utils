# 消息监听

对于Kafka中Topic的数据消费，我们一般都选择使用消息监听器进行消费，怎么把消息监听器玩出花来呢，那就得看看它所实现的功能了。
Spring-Kafka中消息监听大致分为两种类型，一种是单条数据消费，一种是批量消费；两者的区别只是在于监听器一次性获取消息的数量。GenericMessageListener是我们实现消息监听的一个接口，向上扩展的接口有非常多，比如：单数据消费的MessageListener、批量消费的BatchMessageListener、还有具备ACK机制的AcknowledgingMessageListener和BatchAcknowledgingMessageListener等等。接下来我们就一一解析一下。

GenericMessageListener
这里可以看到GenericMessageListener使用注解标明这是一个函数式接口，默认实现了三种不同参数的onMessage方法。data就是我们需要接收的数据，Consumer则是消费者类，Acknowledgment则是用来实现Ack机制的类。这里需要注意一下的是，Consumer对象并不是线程安全的。
@FunctionalInterface
public interface GenericMessageListener<T> {
    void onMessage(T var1);

    default void onMessage(T data, Acknowledgment acknowledgment) {
        throw new UnsupportedOperationException("Container should never call this");
    }

    default void onMessage(T data, Consumer<?, ?> consumer) {
        throw new UnsupportedOperationException("Container should never call this");
    }

    default void onMessage(T data, Acknowledgment acknowledgment, Consumer<?, ?> consumer) {
        throw new UnsupportedOperationException("Container should never call this");
    }
}


接下来先浏览一下继承了GenericMessageListener接口的类。前缀为Batch的接口都是批处理类型的消息监听接口，里面的参数也都讲解过了
public interface MessageListener<K, V> {
    void onMessage(ConsumerRecord<K, V> data);
}

public interface AcknowledgingMessageListener<K, V> { 
    void onMessage(ConsumerRecord<K, V> data, Acknowledgment acknowledgment);
}

public interface ConsumerAwareMessageListener<K, V> extends MessageListener<K, V> {
    void onMessage(ConsumerRecord<K, V> data, Consumer<?, ?> consumer);
}

public interface AcknowledgingConsumerAwareMessageListener<K, V> extends MessageListener<K, V> { 
    void onMessage(ConsumerRecord<K, V> data, Acknowledgment acknowledgment, Consumer<?, ?> consumer);
}

public interface BatchMessageListener<K, V> { 
    void onMessage(List<ConsumerRecord<K, V>> data);
}

public interface BatchAcknowledgingMessageListener<K, V> {
    void onMessage(List<ConsumerRecord<K, V>> data, Acknowledgment acknowledgment);
}

public interface BatchConsumerAwareMessageListener<K, V> extends BatchMessageListener<K, V> { 
    void onMessage(List<ConsumerRecord<K, V>> data, Consumer<?, ?> consumer);
}

public interface BatchAcknowledgingConsumerAwareMessageListener<K, V> extends BatchMessageListener<K, V> { 
    void onMessage(List<ConsumerRecord<K, V>> data, Acknowledgment acknowledgment, Consumer<?, ?> consumer);
}

在把@KafkaListener玩出花前，我们还需要了解怎么使用非注解方式去监听Topic。

我们在创建监听容器前需要创建一个监听容器工厂，这里只需要配置一下消费者工厂就好了
，之后我们使用它去创建我们的监听容器。consumerFactory()这个参数在之前就已经定义过了，这里就不重复贴代码了。
    @Bean
    public ConcurrentKafkaListenerContainerFactory<Integer, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<Integer, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

有了监听容器工厂，我们就可以使用它去创建我们的监听容器
Bean方式创建监听容器
    @Bean
    public KafkaMessageListenerContainer demoListenerContainer() {
        ContainerProperties properties = new ContainerProperties("topic.quick.bean");
        
        properties.setGroupId("bean");
        
        properties.setMessageListener(new MessageListener<Integer,String>() {
            private Logger log = LoggerFactory.getLogger(this.getClass());
            @Override
            public void onMessage(ConsumerRecord<Integer, String> record) {
                log.info("topic.quick.bean receive : " + record.toString());
            }
        });

        return new KafkaMessageListenerContainer(consumerFactory(), properties);
    }

启动项目我们可以看一下控制台的日志，监听容器成功分配给某个消费者的结果很清晰的显示出来了，顺便就写个测试方法测试一下监听器能不能正常运行。
2018-09-11 10:36:15.732  INFO 1168 --- [erContainer-C-1] o.a.k.c.c.internals.AbstractCoordinator  : [Consumer clientId=consumer-4, groupId=bean] Successfully joined group with generation 1
2018-09-11 10:36:15.733  INFO 1168 --- [erContainer-C-1] o.a.k.c.c.internals.ConsumerCoordinator  : [Consumer clientId=consumer-4, groupId=bean] Setting newly assigned partitions [topic.quick.bean-0]
2018-09-11 10:36:15.733  INFO 1168 --- [erContainer-C-1] o.s.k.l.KafkaMessageListenerContainer    : partitions assigned: [topic.quick.bean-0]

    @Test
    public void test() {
        kafkaTemplate.send("topic.quick.bean", "send msg to beanListener");
    }


@KafkaListener参数讲解
在前几章入门的时候就已经写过一个用@KafkaListener注解实现监听的代码，这里就贴一下之前写的代码
    @KafkaListener(id = "demo", topics = "topic.quick.demo")
    public void listen(String msgData) {
        log.info("demo receive : "+msgData);
    }

使用@KafkaListener这个注解并不局限于这个监听容器是单条数据消费还是批量消费，区分单数据还是多数据消费只需要配置一下注解的containerFactory属性即可，先讲解一下这个监听方法都能接收写什么参数吧。

data ： 对于data值的类型其实并没有限定，根据KafkaTemplate所定义的类型来决定。data为List集合的则是用作批量消费。
ConsumerRecord：具体消费数据类，包含Headers信息、分区信息、时间戳等
Acknowledgment：用作Ack机制的接口
Consumer：消费者类，使用该类我们可以手动提交偏移量、控制消费速率等功能
    public void listen1(String data) 

    public void listen2(ConsumerRecord<K,V> data) 

    public void listen3(ConsumerRecord<K,V> data, Acknowledgment acknowledgment) 

    public void listen4(ConsumerRecord<K,V> data, Acknowledgment acknowledgment, Consumer<K,V> consumer) 

    public void listen5(List<String> data) 

    public void listen6(List<ConsumerRecord<K,V>> data) 

    public void listen7(List<ConsumerRecord<K,V>> data, Acknowledgment acknowledgment) 

    public void listen8(List<ConsumerRecord<K,V>> data, Acknowledgment acknowledgment, Consumer<K,V> consumer) 


接下来在看看@KafkaListener的注解都提供了什么属性。
id：消费者的id，当GroupId没有被配置的时候，默认id为GroupId
containerFactory：上面提到了@KafkaListener区分单数据还是多数据消费只需要配置一下注解的containerFactory属性就可以了，这里面配置的是监听容器工厂，也就是ConcurrentKafkaListenerContainerFactory，配置BeanName
topics：需要监听的Topic，可监听多个
topicPartitions：可配置更加详细的监听信息，必须监听某个Topic中的指定分区，或者从offset为200的偏移量开始监听
errorHandler：监听异常处理器，配置BeanName
groupId：消费组ID
idIsGroup：id是否为GroupId
clientIdPrefix：消费者Id前缀
beanRef：真实监听容器的BeanName，需要在 BeanName前加 "__"
public @interface KafkaListener {
    String id() default "";

    String containerFactory() default "";

    String[] topics() default {};

    String topicPattern() default "";

    TopicPartition[] topicPartitions() default {};

    String containerGroup() default "";

    String errorHandler() default "";

    String groupId() default "";

    boolean idIsGroup() default true;

    String clientIdPrefix() default "";

    String beanRef() default "__listener";
}



现在开始才是把监听容器玩出花来的时刻

使用ConsumerRecord类消费
用ConsumerRecord类接收的好处是什么呢，ConsumerRecord类里面包含分区信息、消息头、消息体等内容，如果业务需要获取这些参数时，使用ConsumerRecord会是个不错的选择。如果使用具体的类型接收消息体则更加方便，比如说用String类型去接收消息体。
这里我们编写一个consumerListener方法，监听"topic.quick.consumer" Topic，并把ConsumerRecord里面所包含的内容打印到控制台中
@Component
public class SingleListener {

    private static final Logger log = LoggerFactory.getLogger(SingleListener.class);

    @KafkaListener(id = "consumer", topics = "topic.quick.consumer")
    public void consumerListener(ConsumerRecord<Integer, String> record) {
        log.info("topic.quick.consumer receive : " + record.toString());
    }
}

编写测试方法，发送数据到对应的Topic中，运行测试我们可以看到控制台打印的日志，日志里面包含topic、partition、offset等信息，这其实就是完整的消息储存结构。
    @Test
    public void testConsumerRecord() {
        kafkaTemplate.send("topic.quick.consumer", "test receive by consumerRecord");
    }

2018-09-11 15:52:13.546  INFO 13644 --- [ consumer-0-C-1] com.viu.kafka.listen.SingleListener      : topic.quick.consumer receive : ConsumerRecord(topic = topic.quick.consumer, partition = 0, offset = 0, CreateTime = 1536652333476, serialized key size = -1, serialized value size = 30, headers = RecordHeaders(headers = [], isReadOnly = false), key = null, value = test receive by consumerRecord)


批量消费案例

重新创建一份新的消费者配置，配置为一次拉取5条消息
创建一个监听容器工厂，设置其为批量消费并设置并发量为5，这个并发量根据分区数决定，必须小于等于分区数，否则会有线程一直处于空闲状态
创建一个分区数为8的Topic
创建监听方法，设置消费id为batch，clientID前缀为batch，监听topic.quick.batch，使用batchContainerFactory工厂创建该监听容器

@Component
public class BatchListener {

    private static final Logger log= LoggerFactory.getLogger(BatchListener.class);

    private Map<String, Object> consumerProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "15000");
        //一次拉取消息数量
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "5");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return props;
    }

    @Bean("batchContainerFactory")
    public ConcurrentKafkaListenerContainerFactory listenerContainer() {
        ConcurrentKafkaListenerContainerFactory container = new ConcurrentKafkaListenerContainerFactory();
        container.setConsumerFactory(new DefaultKafkaConsumerFactory(consumerProps()));
        //设置并发量，小于或等于Topic的分区数
        container.setConcurrency(5);
        //设置为批量监听
        container.setBatchListener(true);
        return container;
    }

    @Bean
    public NewTopic batchTopic() {
        return new NewTopic("topic.quick.batch", 8, (short) 1);
    }


    @KafkaListener(id = "batch",clientIdPrefix = "batch",topics = {"topic.quick.batch"},containerFactory = "batchContainerFactory")
    public void batchListener(List<String> data) {
        log.info("topic.quick.batch  receive : ");
        for (String s : data) {
            log.info(  s);
        }
    }

}


紧接着我们启动项目，控制台的日志信息非常完整，我们可以看到batchListener这个监听容器的partition分配信息。我们设置concurrency为5，也就是将会启动5条线程进行监听，那我们创建的topic则是有8个partition，意味着将有3条线程分配到2个partition和2条线程分配到1个partition。我们可以看到这段日志的最后5行，这就是每条线程分配到的partition。
2018-09-11 12:47:49.628  INFO 4708 --- [    batch-2-C-1] o.a.k.c.c.internals.AbstractCoordinator  : [Consumer clientId=batch-2, groupId=batch] Successfully joined group with generation 98
2018-09-11 12:47:49.628  INFO 4708 --- [    batch-2-C-1] o.a.k.c.c.internals.ConsumerCoordinator  : [Consumer clientId=batch-2, groupId=batch] Setting newly assigned partitions [topic.quick.batch-4, topic.quick.batch-5]
2018-09-11 12:47:49.630  INFO 4708 --- [    batch-3-C-1] o.a.k.c.c.internals.AbstractCoordinator  : [Consumer clientId=batch-3, groupId=batch] Successfully joined group with generation 98
2018-09-11 12:47:49.630  INFO 4708 --- [    batch-0-C-1] o.a.k.c.c.internals.AbstractCoordinator  : [Consumer clientId=batch-0, groupId=batch] Successfully joined group with generation 98
2018-09-11 12:47:49.630  INFO 4708 --- [    batch-4-C-1] o.a.k.c.c.internals.AbstractCoordinator  : [Consumer clientId=batch-4, groupId=batch] Successfully joined group with generation 98
2018-09-11 12:47:49.630  INFO 4708 --- [    batch-3-C-1] o.a.k.c.c.internals.ConsumerCoordinator  : [Consumer clientId=batch-3, groupId=batch] Setting newly assigned partitions [topic.quick.batch-6]
2018-09-11 12:47:49.630  INFO 4708 --- [    batch-0-C-1] o.a.k.c.c.internals.ConsumerCoordinator  : [Consumer clientId=batch-0, groupId=batch] Setting newly assigned partitions [topic.quick.batch-0, topic.quick.batch-1]
2018-09-11 12:47:49.630  INFO 4708 --- [    batch-4-C-1] o.a.k.c.c.internals.ConsumerCoordinator  : [Consumer clientId=batch-4, groupId=batch] Setting newly assigned partitions [topic.quick.batch-7]
2018-09-11 12:47:49.631  INFO 4708 --- [    batch-1-C-1] o.a.k.c.c.internals.AbstractCoordinator  : [Consumer clientId=batch-1, groupId=batch] Successfully joined group with generation 98
2018-09-11 12:47:49.631  INFO 4708 --- [    batch-1-C-1] o.a.k.c.c.internals.ConsumerCoordinator  : [Consumer clientId=batch-1, groupId=batch] Setting newly assigned partitions [topic.quick.batch-2, topic.quick.batch-3]
2018-09-11 12:47:49.633  INFO 4708 --- [    batch-3-C-1] o.s.k.l.KafkaMessageListenerContainer    : partitions assigned: [topic.quick.batch-6]
2018-09-11 12:47:49.633  INFO 4708 --- [    batch-0-C-1] o.s.k.l.KafkaMessageListenerContainer    : partitions assigned: [topic.quick.batch-0, topic.quick.batch-1]
2018-09-11 12:47:49.633  INFO 4708 --- [    batch-4-C-1] o.s.k.l.KafkaMessageListenerContainer    : partitions assigned: [topic.quick.batch-7]
2018-09-11 12:47:49.633  INFO 4708 --- [    batch-1-C-1] o.s.k.l.KafkaMessageListenerContainer    : partitions assigned: [topic.quick.batch-2, topic.quick.batch-3]
2018-09-11 12:47:49.634  INFO 4708 --- [    batch-2-C-1] o.s.k.l.KafkaMessageListenerContainer    : partitions assigned: [topic.quick.batch-4, topic.quick.batch-5]


那我们来编写一下测试方法，在短时间内发送12条消息到topic中，可以看到运行结果，对应的监听方法总共拉取了三次数据，其中两次为5条数据，一次为2条数据，加起来就是我们在测试方法发送的12条数据。证明我们的批量消费方法是按预期进行的。
    @Autowired
    private KafkaTemplate kafkaTemplate;

    @Test
    public void testBatch() {
        for (int i = 0; i < 12; i++) {
            kafkaTemplate.send("topic.quick.batch", "test batch listener,dataNum-" + i);
        }
    }

2018-09-11 12:08:51.840  INFO 12416 --- [    batch-0-C-1] com.viu.kafka.listen.BatchListener       : topic.quick.batch  receive : 
2018-09-11 12:08:51.840  INFO 12416 --- [    batch-0-C-1] com.viu.kafka.listen.BatchListener       : test batch listener,dataNum-5
2018-09-11 12:08:51.840  INFO 12416 --- [    batch-0-C-1] com.viu.kafka.listen.BatchListener       : test batch listener,dataNum-2
2018-09-11 12:08:51.840  INFO 12416 --- [    batch-0-C-1] com.viu.kafka.listen.BatchListener       : test batch listener,dataNum-10
2018-09-11 12:08:51.840  INFO 12416 --- [    batch-0-C-1] com.viu.kafka.listen.BatchListener       : test batch listener,dataNum-6
2018-09-11 12:08:51.840  INFO 12416 --- [    batch-0-C-1] com.viu.kafka.listen.BatchListener       : test batch listener,dataNum-3
2018-09-11 12:08:51.841  INFO 12416 --- [    batch-0-C-1] com.viu.kafka.listen.BatchListener       : topic.quick.batch  receive : 
2018-09-11 12:08:51.841  INFO 12416 --- [    batch-0-C-1] com.viu.kafka.listen.BatchListener       : test batch listener,dataNum-11
2018-09-11 12:08:51.841  INFO 12416 --- [    batch-0-C-1] com.viu.kafka.listen.BatchListener       : test batch listener,dataNum-0
2018-09-11 12:08:51.841  INFO 12416 --- [    batch-0-C-1] com.viu.kafka.listen.BatchListener       : test batch listener,dataNum-8
2018-09-11 12:08:51.841  INFO 12416 --- [    batch-0-C-1] com.viu.kafka.listen.BatchListener       : test batch listener,dataNum-7
2018-09-11 12:08:51.841  INFO 12416 --- [    batch-0-C-1] com.viu.kafka.listen.BatchListener       : test batch listener,dataNum-4
2018-09-11 12:08:51.842  INFO 12416 --- [    batch-0-C-1] com.viu.kafka.listen.BatchListener       : topic.quick.batch  receive : 
2018-09-11 12:08:51.842  INFO 12416 --- [    batch-0-C-1] com.viu.kafka.listen.BatchListener       : test batch listener,dataNum-1
2018-09-11 12:08:51.842  INFO 12416 --- [    batch-0-C-1] com.viu.kafka.listen.BatchListener       : test batch listener,dataNum-9

注意：设置的并发量不能大于partition的数量，如果需要提高吞吐量，可以通过增加partition的数量达到快速提升吞吐量的效果。

监听Topic中指定的分区
紧接着刚才编写的代码里面编写新的监听器，第一眼看到这代码，妈呀，这注解这么长，哈哈哈，我也不是故意的啊。
这里使用@KafkaListener注解的topicPartitions属性监听不同的partition分区。
@TopicPartition：topic--需要监听的Topic的名称，partitions --需要监听Topic的分区id，
partitionOffsets --可以设置从某个偏移量开始监听
@PartitionOffset：partition --分区Id，非数组，initialOffset --初始偏移量
    @Bean
    public NewTopic batchWithPartitionTopic() {
        return new NewTopic("topic.quick.batch.partition", 8, (short) 1);
    }

    @KafkaListener(id = "batchWithPartition",clientIdPrefix = "bwp",containerFactory = "batchContainerFactory",
        topicPartitions = {
            @TopicPartition(topic = "topic.quick.batch.partition",partitions = {"1","3"}),
            @TopicPartition(topic = "topic.quick.batch.partition",partitions = {"0","4"},
                    partitionOffsets = @PartitionOffset(partition = "2",initialOffset = "100"))
        }
    )
    public void batchListenerWithPartition(List<String> data) {
        log.info("topic.quick.batch.partition  receive : ");
        for (String s : data) {
            log.info(s);
        }
    }


其实和我们刚才写的批量消费区别只是在注解上多了个属性，启动项目我们仔细搜索一下控制台输出的日志，如果存在该日志则说明成功。同样的我们往这个Topic里面写入一些数据，运行后我们可以看到控制台只监听到一部分消息，这是因为创建的Topic的partition数量为8，而我们只监听了0、1、2、3、4这几个partition，也就是说5 6  7这三个分区的消息我们并没有读取出来。
2018-09-11 14:39:52.045  INFO 12412 --- [Partition-4-C-1] o.a.k.c.consumer.internals.Fetcher       : [Consumer clientId=bwp-4, groupId=batchWithPartition] Fetch offset 100 is out of range for partition topic.quick.batch-2, resetting offset

    @Test
    public void testBatch() throws InterruptedException {
        for (int i = 0; i < 12; i++) {
            kafkaTemplate.send("topic.quick.batch.partition", "test batch listener,dataNum-" + i);
        }
    }

2018-09-11 14:51:09.063  INFO 1532 --- [Partition-2-C-1] com.viu.kafka.listen.BatchListener       : topic.quick.batch.partition  receive : 
2018-09-11 14:51:09.063  INFO 1532 --- [Partition-2-C-1] com.viu.kafka.listen.BatchListener       : test batch listener,dataNum-4
2018-09-11 14:51:09.064  INFO 1532 --- [Partition-1-C-1] com.viu.kafka.listen.BatchListener       : topic.quick.batch.partition  receive : 
2018-09-11 14:51:09.064  INFO 1532 --- [Partition-1-C-1] com.viu.kafka.listen.BatchListener       : test batch listener,dataNum-2
2018-09-11 14:51:09.075  INFO 1532 --- [Partition-0-C-1] com.viu.kafka.listen.BatchListener       : topic.quick.batch.partition  receive : 
2018-09-11 14:51:09.075  INFO 1532 --- [Partition-0-C-1] com.viu.kafka.listen.BatchListener       : test batch listener,dataNum-1
2018-09-11 14:51:09.078  INFO 1532 --- [Partition-1-C-1] com.viu.kafka.listen.BatchListener       : topic.quick.batch.partition  receive : 
2018-09-11 14:51:09.078  INFO 1532 --- [Partition-1-C-1] com.viu.kafka.listen.BatchListener       : test batch listener,dataNum-10
2018-09-11 14:51:09.091  INFO 1532 --- [Partition-4-C-1] com.viu.kafka.listen.BatchListener       : topic.quick.batch.partition  receive : 
2018-09-11 14:51:09.091  INFO 1532 --- [Partition-4-C-1] com.viu.kafka.listen.BatchListener       : test batch listener,dataNum-5
2018-09-11 14:51:09.095  INFO 1532 --- [Partition-0-C-1] com.viu.kafka.listen.BatchListener       : topic.quick.batch.partition  receive : 
2018-09-11 14:51:09.096  INFO 1532 --- [Partition-0-C-1] com.viu.kafka.listen.BatchListener       : test batch listener,dataNum-9
2018-09-11 14:51:09.097  INFO 1532 --- [Partition-3-C-1] com.viu.kafka.listen.BatchListener       : topic.quick.batch.partition  receive : 
2018-09-11 14:51:09.098  INFO 1532 --- [Partition-3-C-1] com.viu.kafka.listen.BatchListener       : test batch listener,dataNum-7


注解方式获取消息头及消息体
当你接收的消息包含请求头，以及你监听方法需要获取该消息非常多的字段时可以通过这种方式，毕竟get方法代码量还是稍多点的。这里使用的是默认的监听容器工厂创建的，如果你想使用批量消费，把对应的类型改为List即可，比如List<String> data ， List<Integer> key。
@Payload：获取的是消息的消息体，也就是发送内容
@Header(KafkaHeaders.RECEIVED_MESSAGE_KEY)：获取发送消息的key
@Header(KafkaHeaders.RECEIVED_PARTITION_ID)：获取当前消息是从哪个分区中监听到的
@Header(KafkaHeaders.RECEIVED_TOPIC)：获取监听的TopicName
@Header(KafkaHeaders.RECEIVED_TIMESTAMP)：获取时间戳
    @KafkaListener(id = "anno", topics = "topic.quick.anno")
    public void annoListener(@Payload String data,
                             @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) Integer key,
                             @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
                             @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                             @Header(KafkaHeaders.RECEIVED_TIMESTAMP) long ts) {
        log.info("topic.quick.anno receive : \n"+
            "data : "+data+"\n"+
            "key : "+key+"\n"+
            "partitionId : "+partition+"\n"+
            "topic : "+topic+"\n"+
            "timestamp : "+ts+"\n"
        );

    }

监听容器编写好了，那就写个测试方法测试一下。启动测试后可以看到监听方法成功的把我们所需要的数据提取出来了，说明这段代码也是ojbk的。
    @Test
    public void testAnno() throws InterruptedException {
        Map map = new HashMap<>();
        map.put(KafkaHeaders.TOPIC, "topic.quick.anno");
        map.put(KafkaHeaders.MESSAGE_KEY, 0);
        map.put(KafkaHeaders.PARTITION_ID, 0);
        map.put(KafkaHeaders.TIMESTAMP, System.currentTimeMillis());

        kafkaTemplate.send(new GenericMessage<>("test anno listener", map));
    }

2018-09-11 15:27:47.108  INFO 7592 --- [     anno-0-C-1] com.viu.kafka.listen.SingleListener      : topic.quick.anno receive : 
data : test anno listener
key : 0
partitionId : 0
topic : topic.quick.anno
timestamp : 1536650867015


使用Ack机制确认消费
Kafka的Ack机制相对于RabbitMQ的Ack机制差别比较大，刚入门Kafka的时候我也被搞蒙了，不过能弄清楚Kafka是怎么消费消息的就能理解Kafka的Ack机制了
我先说说RabbitMQ的Ack机制，RabbitMQ的消费可以说是一次性的，也就是你确认消费后就立刻从硬盘或内存中删除，而且RabbitMQ粗糙点来说是顺序消费，像排队一样，一个个顺序消费，未被确认的消息则会重新回到队列中，等待监听器再次消费。
但Kafka不同，Kafka是通过最新保存偏移量进行消息消费的，而且确认消费的消息并不会立刻删除，所以我们可以重复的消费未被删除的数据，当第一条消息未被确认，而第二条消息被确认的时候，Kafka会保存第二条消息的偏移量，也就是说第一条消息再也不会被监听器所获取，除非是根据第一条消息的偏移量手动获取。

使用Kafka的Ack机制比较简单，只需简单的三步即可：

设置ENABLE_AUTO_COMMIT_CONFIG=false，禁止自动提交
设置AckMode=MANUAL_IMMEDIATE
监听方法加入Acknowledgment ack 参数

怎么拒绝消息呢，只要在监听方法中不调用ack.acknowledge()即可
@Component
public class AckListener {

    private static final Logger log= LoggerFactory.getLogger(AckListener.class);

    private Map<String, Object> consumerProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "15000");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return props;
    }

    @Bean("ackContainerFactory")
    public ConcurrentKafkaListenerContainerFactory ackContainerFactory() {
        ConcurrentKafkaListenerContainerFactory factory = new ConcurrentKafkaListenerContainerFactory();
        factory.setConsumerFactory(new DefaultKafkaConsumerFactory(consumerProps()));
        factory.getContainerProperties().setAckMode(AbstractMessageListenerContainer.AckMode.MANUAL_IMMEDIATE);
        factory.setConsumerFactory(new DefaultKafkaConsumerFactory(consumerProps()));
        return factory;
    }


    @KafkaListener(id = "ack", topics = "topic.quick.ack",containerFactory = "ackContainerFactory")
    public void ackListener(ConsumerRecord record, Acknowledgment ack) {
        log.info("topic.quick.ack receive : " + record.value());
        ack.acknowledge();
    }
}



编写测试方法，运行后可以方法监听方法能收到消息，紧接着注释ack.acknowledge()方法，重新测试，同样你会发现监听容器能接收到消息，这个时候如果你重启项目还是可以看到未被确认的那几条消息。
    @Test
    public void testAck() throws InterruptedException {
        for (int i = 0; i < 5; i++) {
            kafkaTemplate.send("topic.quick.ack", i+"");
        }
    }

在这段章节开头之初我就讲解了Kafka机制会出现的一些情况，导致没办法重复消费未被Ack的消息，解决办法有如下：

重新将消息发送到队列中，这种方式比较简单而且可以使用Headers实现第几次消费的功能，用以下次判断

    @KafkaListener(id = "ack", topics = "topic.quick.ack", containerFactory = "ackContainerFactory")
    public void ackListener(ConsumerRecord record, Acknowledgment ack, Consumer consumer) {
        log.info("topic.quick.ack receive : " + record.value());

        //如果偏移量为偶数则确认消费，否则拒绝消费
        if (record.offset() % 2 == 0) {
            log.info(record.offset()+"--ack");
            ack.acknowledge();
        } else {
            log.info(record.offset()+"--nack");
            kafkaTemplate.send("topic.quick.ack", record.value());
        }
    }


使用Consumer.seek方法，重新回到该未ack消息偏移量的位置重新消费，这种可能会导致死循环，原因出现于业务一直没办法处理这条数据，但还是不停的重新定位到该数据的偏移量上。

    @KafkaListener(id = "ack", topics = "topic.quick.ack", containerFactory = "ackContainerFactory")
    public void ackListener(ConsumerRecord record, Acknowledgment ack, Consumer consumer) {
        log.info("topic.quick.ack receive : " + record.value());

        //如果偏移量为偶数则确认消费，否则拒绝消费
        if (record.offset() % 2 == 0) {
            log.info(record.offset()+"--ack");
            ack.acknowledge();
        } else {
            log.info(record.offset()+"--nack");
            consumer.seek(new TopicPartition("topic.quick.ack",record.partition()),record.offset() );
        }
    }

如果有更好的办法可以留言，感谢

作者：viu_astray
链接：https://www.jianshu.com/p/a64defb44a23
来源：简书
简书著作权归作者所有，任何形式的转载都请联系作者获得授权并注明出处。