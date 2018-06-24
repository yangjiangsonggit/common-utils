package com.yjs.commonutils.kafka08;

import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.message.MessageAndMetadata;
import java.util.Iterator;

/**
 * create by jiangsongy on 2018/6/24
 */
public class ConsumerStreamWorker implements Runnable {

    private Iterator<KafkaStream<byte[], byte[]>> topicStreamsIterator;

    public ConsumerStreamWorker(Iterator<KafkaStream<byte[], byte[]>> topicStreamsIterator) {
        this.topicStreamsIterator = topicStreamsIterator;
    }

    @Override
    public void run() {
        while (topicStreamsIterator.hasNext()) {    //监控该topic是不是有新的输入流
            KafkaStream<byte[], byte[]> kafkaStream = topicStreamsIterator.next();
            ConsumerIterator<byte[], byte[]> streamIterator = kafkaStream.iterator();
            while (streamIterator.hasNext()) {  //消费流中的message
                MessageAndMetadata<byte[], byte[]> record = streamIterator.next();
                String message = new String(record.message());
                if (record.topic().equals("mykafka_test1")) {
                    System.out.println("mykafka_test1 -> " + record.topic() + "=========================");
                    System.out.println("message: " + message);
                    System.out.println("record: " + record.toString());
                }else if (record.topic().equals("mykafka_test2")){
                    System.out.println("mykafka_test2 -> " + record.topic() + "=========================");
                    System.out.println("message: " + message);
                    System.out.println("record: " + record.toString());
                }
            }

        }
    }
}
