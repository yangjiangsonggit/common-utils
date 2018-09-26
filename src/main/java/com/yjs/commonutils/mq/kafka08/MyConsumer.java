package com.yjs.commonutils.mq.kafka08;

import com.google.common.collect.Maps;
import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * create by jiangsongy on 2018/6/24
 */
public class MyConsumer implements Runnable{

    private List<String> topics;

    private ConsumerConnector consumer;

    private ExecutorService executor;

    public MyConsumer(List<String> topics) {
        this.consumer = this.getConsumer(this.getConsumerConfig());
        this.topics = topics;
        this.executor = Executors.newFixedThreadPool(topics.size());
    }

    private ConsumerConfig getConsumerConfig(){
        Properties properties = new Properties();
        properties.put("zookeeper.connect", "localhost:2181");
        properties.put("group.id", "mykafka_consumer");
        properties.put("zookeeper.session.timeout.ms", "4000");
        properties.put("zookeeper.sync.time.ms", "200");
        properties.put("auto.commit.interval.ms", "1000");
        properties.put("auto.offset.reset", "smallest");
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        return new ConsumerConfig(properties);
    }

    private ConsumerConnector getConsumer(ConsumerConfig consumerConfig){
        return (ConsumerConnector) Consumer.createJavaConsumerConnector(consumerConfig);
    }

    @Override
    public void run() {
        Map<String, Integer> topicCountMap = Maps.newHashMap();
        for (String topic : topics) {
            topicCountMap.put(topic,1);
        }

        Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumer.createMessageStreams(topicCountMap);
        for (Map.Entry<String, List<KafkaStream<byte[], byte[]>>> entry : consumerMap.entrySet()) {
            String topic = entry.getKey();
            System.out.println("topic:" + topic);
            List<KafkaStream<byte[], byte[]>> topicStreams = entry.getValue();

            //这里如果有多个topic需要新开线程,不然会一直阻塞,只有一个topic能取到值
            Iterator<KafkaStream<byte[], byte[]>> topicStreamsIterator = topicStreams.iterator();
            ConsumerStreamWorker consumerStreamWorker = new ConsumerStreamWorker(topicStreamsIterator);

            executor.submit(consumerStreamWorker);
        }

    }
}
