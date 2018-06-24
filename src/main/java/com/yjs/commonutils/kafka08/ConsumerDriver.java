package com.yjs.commonutils.kafka08;

import com.google.common.collect.Lists;
import java.util.List;

/**
 * create by jiangsongy on 2018/6/24
 */
public class ConsumerDriver {

    public static void main(String[] args) {

        List<String> topics = Lists.newArrayList("mykafka_test1","mykafka_test2");
        MyConsumer myConsumer = new MyConsumer(topics);
        myConsumer.run();
    }
}
