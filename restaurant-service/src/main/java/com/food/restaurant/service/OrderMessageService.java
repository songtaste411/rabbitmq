package com.food.restaurant.service;

import com.alibaba.fastjson.JSON;
import com.rabbitmq.client.*;
import lombok.extern.slf4j.Slf4j;
import com.food.restaurant.dao.ProductDao;
import com.food.restaurant.dao.RestaurantDao;
import com.food.restaurant.dto.OrderMessageDTO;
import com.food.restaurant.enums.ProductStatus;
import com.food.restaurant.enums.RantaurantStatus;
import com.food.restaurant.po.ProductPo;
import com.food.restaurant.po.RestaurantPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

@Slf4j
@Service
public class OrderMessageService {
    @Autowired
    private ProductDao productDao;
    @Autowired
    private RestaurantDao restaurantDao;
    @Autowired
    private Channel channel;
    @Async
    public void handMessage() throws IOException, TimeoutException, InterruptedException {
            //队列绑定关系
            channel.exchangeDeclare(
                    "exchange.order.restaurant",
                    BuiltinExchangeType.DIRECT,
                    Boolean.TRUE,
                    Boolean.FALSE,
                    null
            );
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("x-message-ttl",15000);
        channel.queueDeclare(
                    "queue.restaurant",
                    Boolean.TRUE,
                    Boolean.FALSE,
                    Boolean.FALSE,
                hashMap
            );
            channel.queueBind(
                    "queue.restaurant",
                    "exchange.order.restaurant",
                    "key.restaurant"
            );

            //开启消费端限流
            channel.basicQos(2);
            channel.basicConsume("queue.restaurant", Boolean.FALSE,deliverCallback,consumerTag->{});
            while(true){
                Thread.sleep(100000000);
            }

    }
    DeliverCallback deliverCallback=((consumerTag,message)->{
        try {
            OrderMessageDTO messageDTO = JSON.parseObject(message.getBody(), OrderMessageDTO.class);
            ProductPo productPo = productDao.selectProduct(messageDTO.getProductId());
            RestaurantPo restaurantPo = restaurantDao.selectRestaurant(productPo.getRestaurantId());
            if(productPo.getStatus().equals(ProductStatus.AVALIABLE.name())&& restaurantPo.getStatus().equals(RantaurantStatus.OPEN.name())){
                messageDTO.setConfirmed(Boolean.TRUE);
                messageDTO.setPrice(productPo.getPrice());
            }else{
                messageDTO.setConfirmed(Boolean.FALSE);
            }
            //当我们发送的消息无法路由的时候，handleReturn会被调用
//            channel.addReturnListener(new ReturnListener() {
//                @Override
//                public void handleReturn(int replyCode, String replyText, String exchange, String routingKey, AMQP.BasicProperties properties, byte[] body) throws IOException {
//                    log.info("Message Return: replyCode:{}, replyText: {}, exchange: {}, routingKey: {}, properties: {}, body: {}",replyCode, replyText, exchange, routingKey, properties, body);
//                }
//            });
            channel.addReturnListener(new ReturnCallback() {
                @Override
                public void handle(Return aReturn) {
                    log.info("Message Return:{}",aReturn.getReplyText());
                }
            });
            Thread.sleep(3000);
            //签收消息
//            channel.basicAck(message.getEnvelope().getDeliveryTag(),Boolean.FALSE);
            //拒收消息
            //channel.basicNack(message.getEnvelope().getDeliveryTag(),Boolean.FALSE,Boolean.TRUE);
            String messageToSend = JSON.toJSONString(messageDTO);
            channel.basicPublish(
                    "exchange.order.restaurant",
                    "key.order",
                    Boolean.TRUE,
                    null,
                    messageToSend.getBytes()
            );
            log.info("消息开始发送"+messageToSend);
            //避免跳出try代码块channel自动被关掉，导致无法调用ReturnListener,这种叫做autoClosable
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


    });

}
