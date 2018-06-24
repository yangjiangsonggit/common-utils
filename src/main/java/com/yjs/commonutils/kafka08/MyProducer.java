package com.yjs.commonutils.kafka08;

import com.google.common.base.Joiner;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

/**
 * create by jiangsongy on 2018/6/24
 */
public class MyProducer implements Runnable{

    private KafkaProducer<String, String> producer;

    private List<String> brokersStr;

    private List<String> mockData;

    private String topic;

    public MyProducer( List<String> brokersStr, List<String> mockData,
                      String topic) {
        this.brokersStr = brokersStr;
        this.mockData = mockData;
        this.topic = topic;
        this.producer = this.getConsumerConfig();
    }

    private KafkaProducer<String, String> getConsumerConfig(){
        Properties properties = new Properties();
        properties.put("bootstrap.servers",Joiner.on(",").skipNulls().join(this.brokersStr));
        properties.put("client.id","mykafka_producer");
        properties.put("retries",0);
        properties.put("batch.size",32768);
        properties.put("linger.ms",30);
        properties.put("buffer.memory",8388608);
        properties.put("key.serializer","org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer","org.apache.kafka.common.serialization.StringSerializer");
        return new KafkaProducer(properties);
    }

    @Override
    public void run() {
        mockData.forEach(messageObject -> {
            try {
                producer.send(new ProducerRecord<>(topic, messageObject)).get();
                System.out.println("Sent message: " + messageObject);
            } catch (InterruptedException | ExecutionException e) {
                System.out.println("Sent message error: cause by " + e.getMessage());
            }
        });
    }
}
