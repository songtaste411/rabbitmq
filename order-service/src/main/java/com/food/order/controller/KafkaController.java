package com.food.order.controller;

import io.swagger.annotations.ApiOperation;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.errors.AuthorizationException;
import org.apache.kafka.common.errors.OutOfOrderSequenceException;
import org.apache.kafka.common.errors.ProducerFencedException;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Properties;

@RestController
public class KafkaController {

    private final static String TOPIC_NAME = "my-replicated-topic";

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @RequestMapping("/send")
    public void send() {
        kafkaTemplate.send(TOPIC_NAME, 0, "key", "this is a msg");
    }
    @ApiOperation("事务测试")
    @RequestMapping("/tran")
    public void tran() {

        Properties props = new Properties();
        props.put("bootstrap.servers", "192.168.130.130:9093");
        props.put("transactional.id", "my-transactional-id");
        Producer<String, String> producer = new KafkaProducer<>(props, new StringSerializer(), new StringSerializer());
        //初始化事务
        producer.initTransactions();

        try {
            //开启事务
            producer.beginTransaction();
            for (int i = 0; i < 100; i++){
                //发到不同的主题的不同分区
                producer.send(new ProducerRecord<>("hdfs-topic", Integer.toString(i), Integer.toString(i)));
                producer.send(new ProducerRecord<>("es-topic", Integer.toString(i), Integer.toString(i)));
                producer.send(new ProducerRecord<>("redis-topic", Integer.toString(i), Integer.toString(i)));
            }
            //提交事务
            producer.commitTransaction();
        } catch (ProducerFencedException | OutOfOrderSequenceException | AuthorizationException e) {
            // We can't recover from these exceptions, so our only option is to close the producer and exit.
            producer.close();
        } catch (KafkaException e) {
            // For all other exceptions, just abort the transaction and try again.
            //回滚事务
            System.out.println("回滚事务");
            producer.abortTransaction();
        }
        producer.close();
    }
}