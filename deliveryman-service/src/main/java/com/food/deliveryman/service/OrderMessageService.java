package com.food.deliveryman.service;

import com.alibaba.fastjson.JSON;
import com.rabbitmq.client.*;
import com.food.deliveryman.dao.DeliverymanDao;
import com.food.deliveryman.dto.OrderMessageDTO;
import com.food.deliveryman.enums.DeliverymanStatus;
import com.food.deliveryman.po.DeliverymanPo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;
@Slf4j
@Service
public class OrderMessageService {
    @Autowired
    private DeliverymanDao deliverymanDao;
    @Async
    public void handMessage() throws IOException, TimeoutException, InterruptedException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");


        try(Connection connection=connectionFactory.newConnection();
            Channel channel=connection.createChannel() ){
            //队列绑定关系
            channel.exchangeDeclare(
                    "exchange.order.deliveryman",
                    BuiltinExchangeType.DIRECT,
                    Boolean.TRUE,
                    Boolean.FALSE,
                    null
            );
            channel.queueDeclare(
                    "queue.deliveryman",
                    Boolean.TRUE,
                    Boolean.FALSE,
                    Boolean.FALSE,
                    null
            );
            channel.queueBind(
                    "queue.deliveryman",
                    "exchange.order.deliveryman",
                    "key.deliveryman"
            );


            channel.basicConsume("queue.deliveryman", Boolean.TRUE,deliverCallback,consumerTag->{});
            while(true){
                Thread.sleep(100000000);
            }
        }
    }
    DeliverCallback deliverCallback=((consumerTag, message)->{
        OrderMessageDTO messageDTO = JSON.parseObject(message.getBody(), OrderMessageDTO.class);
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");

        try(Connection connection=connectionFactory.newConnection();
            Channel channel=connection.createChannel() ){
            String messageToSend = JSON.toJSONString(messageDTO);
            //查询所有空暇骑手
//            List<DeliverymanPo> deliverymanPos = deliverymanDao.selectDeliverymanBystatus(DeliverymanStatus.AVALIABLE);
//            messageDTO.setDeliverymanId(deliverymanPos.get(0).getId());
            messageDTO.setDeliverymanId(1);
            channel.basicPublish(
                    "exchange.order.deliveryman",
                    "key.deliveryman",
                    null,
                    messageToSend.getBytes()
            );


        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }


    });

}
