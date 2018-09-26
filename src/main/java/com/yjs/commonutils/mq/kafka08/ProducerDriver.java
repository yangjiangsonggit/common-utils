package com.yjs.commonutils.mq.kafka08;

import com.google.common.collect.Lists;
import java.util.List;

/**
 * create by jiangsongy on 2018/6/24
 */
public class ProducerDriver {

    public static void main(String[] args) {

        List<String> brokersStr = Lists.newArrayList("localhost:9092");
        List<String> mockData = Lists.newArrayList();
        for (int i = 100; i < 200; i++) {
            mockData.add("mock" + i);
        }
        String topic = "mykafka_test1";
//        String topic = "mykafka_test2";

        MyProducer myProducer = new MyProducer(brokersStr,mockData,topic);
        myProducer.run();
    }
}
