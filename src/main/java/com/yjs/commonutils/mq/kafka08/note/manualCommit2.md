之前使用kafka的KafkaStream：http://blog.csdn.net/qq_20641565/article/details/60810174，让每个消费者和对应的patition建立对应的流来读取kafka上面的数据，如果comsumer得到数据，那么kafka就会自动去维护该comsumer的offset，例如在获取到kafka的消息后正准备入库（未入库），但是消费者挂了，那么如果让kafka自动去维护offset，它就会认为这条数据已经被消费了，那么会造成数据丢失。

但是kafka可以让你自己去手动提交，如果在上面的场景中，那么需要我们手动commit，如果comsumer挂了 那么程序就不会执行commit这样的话 其他同group的消费者又可以消费这条数据，保证数据不丢，先要做如下设置：



//设置不自动提交，自己手动更新offset
properties.put("enable.auto.commit", "false");12

使用如下api提交：



consumer.commitSync();1



注意：

刚做了个测试，如果我从kafka中取出5条数据，分别为1,2,3,4,5，如果消费者在执行一些逻辑在执行1,2,3，4的时候都失败了未提交commit，然后消费5做逻辑成功了提交了commit，那么offset也会被移动到5那一条数据那里，1,2,3,4 相当于也会丢失

如果是做消费者取出数据执行一些操作，全部都失败的话，然后重启消费者，这些数据会从失败的时候重新开始读取

所以消费者还是应该自己做容错机制

测试项目结构如下:



其中ConsumerThreadNew类：

package com.lijie.kafka;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 *                       
 * @Filename ConsumerThreadNew.java
 *
 * @Description 
 *
 * @Version 1.0
 *
 * @Author Lijie
 *
 * @Email lijiewj39069@touna.cn
 *       
 * @History
 *<li>Author: Lijie</li>
 *<li>Date: 2017年3月21日</li>
 *<li>Version: 1.0</li>
 *<li>Content: create</li>
 *
 */
public class ConsumerThreadNew implements Runnable {
    private static Logger                   LOG = LoggerFactory.getLogger(ConsumerThreadNew.class);

    //KafkaConsumer kafka生产者
    private KafkaConsumer<String, String>   consumer;

    //消费者名字
    private String                          name;

    //消费的topic组
    private List<String>                    topics;

    //构造函数
    public ConsumerThreadNew(KafkaConsumer<String, String> consumer, String topic, String name) {
        super();
        this.consumer = consumer;
        this.name = name;
        this.topics = Arrays.asList(topic);
    }

    @Override
    public void run() {
        consumer.subscribe(topics);
        List<ConsumerRecord<String, String>> buffer = new ArrayList<>();

        // 批量提交数量
        final int minBatchSize = 1; 
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(100);
            for (ConsumerRecord<String, String> record : records) {
                LOG.info("消费者的名字为:" + name + ",消费的消息为：" + record.value());
                buffer.add(record);
            }
            if (buffer.size() >= minBatchSize) {
                //这里就是处理成功了然后自己手动提交
                consumer.commitSync();
                LOG.info("提交完毕");
                buffer.clear();
            }
        }
    }

}

MyConsume类如下：



package com.lijie.kafka;

import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 *                       
 * @Filename MyConsume.java
 *
 * @Description 
 *
 * @Version 1.0
 *
 * @Author Lijie
 *
 * @Email lijiewj39069@touna.cn
 *       
 * @History
 *<li>Author: Lijie</li>
 *<li>Date: 2017年3月21日</li>
 *<li>Version: 1.0</li>
 *<li>Content: create</li>
 *
 */
public class MyConsume {
    private static Logger   LOG = LoggerFactory.getLogger(MyConsume.class);

    public MyConsume() {
        // TODO Auto-generated constructor stub
    }

    public static void main(String[] args) {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", "10.0.4.141:19093,10.0.4.142:19093,10.0.4.143:19093");
        //设置不自动提交，自己手动更新offset
        properties.put("enable.auto.commit", "false");
        properties.put("auto.offset.reset", "latest");
        properties.put("zookeeper.connect", "10.0.4.141:2181,10.0.4.142:2181,10.0.4.143:2181");
        properties.put("session.timeout.ms", "30000");
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("group.id", "lijieGroup");
        properties.put("zookeeper.connect", "192.168.80.123:2181");
        properties.put("auto.commit.interval.ms", "1000");
        ExecutorService executor = Executors.newFixedThreadPool(5);

        //执行消费
        for (int i = 0; i < 7; i++) {
            executor.execute(new ConsumerThreadNew(new KafkaConsumer<String, String>(properties),
                "lijietest", "消费者" + (i + 1)));
        }
    }
}

MyProducer类如下：



package com.lijie.kafka;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

/**
 * 
 *                       
 * @Filename MyProducer.java
 *
 * @Description 
 *
 * @Version 1.0
 *
 * @Author Lijie
 *
 * @Email lijiewj39069@touna.cn
 *       
 * @History
 *<li>Author: Lijie</li>
 *<li>Date: 2017年3月21日</li>
 *<li>Version: 1.0</li>
 *<li>Content: create</li>
 *
 */
public class MyProducer {

    private static Properties                       properties;
    private static KafkaProducer<String, String>    pro;
    static {
        //配置
        properties = new Properties();
        properties.put("bootstrap.servers", "10.0.4.141:19093,10.0.4.142:19093,10.0.4.143:19093");

        //序列化类型
        properties
            .put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        //创建生产者
        pro = new KafkaProducer<>(properties);
    }

    public static void main(String[] args) throws Exception {
        produce("lijietest");
    }

    public static void produce(String topic) throws Exception {

        //模拟message
        //          String value = UUID.randomUUID().toString();
        for (int i = 0; i < 10000; i++) {
            //封装message
            ProducerRecord<String, String> pr = new ProducerRecord<String, String>(topic, i + "");

            //发送消息
            pro.send(pr);
            Thread.sleep(1000);
        }

    }
}

pom文件如下：



<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>lijie-kafka-offset</groupId>
    <artifactId>lijie-kafka-offset</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <dependencies>
        <dependency>
            <groupId>org.apache.kafka</groupId>
            <artifactId>kafka_2.11</artifactId>
            <version>0.10.1.1</version>
        </dependency>
        <dependency>
            <groupId>org.apache.hadoop</groupId>
            <artifactId>hadoop-common</artifactId>
            <version>2.2.0</version>
        </dependency>
        <dependency>
            <groupId>org.apache.hadoop</groupId>
            <artifactId>hadoop-hdfs</artifactId>
            <version>2.2.0</version>
        </dependency>
        <dependency>
            <groupId>org.apache.hadoop</groupId>
            <artifactId>hadoop-client</artifactId>
            <version>2.2.0</version>
        </dependency>
        <dependency>
            <groupId>org.apache.hbase</groupId>
            <artifactId>hbase-client</artifactId>
            <version>1.0.3</version>
        </dependency>
        <dependency>
            <groupId>org.apache.hbase</groupId>
            <artifactId>hbase-server</artifactId>
            <version>1.0.3</version>
        </dependency>
        <dependency>
            <groupId>org.apache.hadoop</groupId>
            <artifactId>hadoop-hdfs</artifactId>
            <version>2.2.0</version>
        </dependency>
        <dependency>
            <groupId>jdk.tools</groupId>
            <artifactId>jdk.tools</artifactId>
            <version>1.7</version>
            <scope>system</scope>
            <systemPath>${JAVA_HOME}/lib/tools.jar</systemPath>
        </dependency>
        <dependency>
            <groupId>org.apache.httpcomponents</groupId>
            <artifactId>httpclient</artifactId>
            <version>4.3.6</version>
        </dependency>
    </dependencies>
    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <configuration>
                    <source>1.7</source>
                    <target>1.7</target>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>

---------------------

本文来自 lijie_cq 的CSDN 博客 ，全文地址请点击：https://blog.csdn.net/qq_20641565/article/details/64440425?utm_source=copy 