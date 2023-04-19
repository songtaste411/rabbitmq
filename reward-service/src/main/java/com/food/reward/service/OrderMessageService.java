package com.food.reward.service;

import com.alibaba.fastjson.JSON;
import com.food.reward.dao.RewardDao;
import com.food.reward.dto.OrderMessageDTO;
import com.food.reward.enums.RewardStatus;
import com.food.reward.po.RewardPo;
import com.rabbitmq.client.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeoutException;

@Slf4j
@Service
public class OrderMessageService {
    @Autowired
    private RewardDao rewardDao;
    @Async
    public void handMessage() throws IOException, TimeoutException, InterruptedException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");


        try(Connection connection=connectionFactory.newConnection();
            Channel channel=connection.createChannel() ){
            //监听的exchange
            channel.exchangeDeclare(
                    "exchange.order.reward",
                    BuiltinExchangeType.TOPIC,
                    Boolean.TRUE,
                    Boolean.FALSE,
                    null
            );
            //监听的queue
            channel.queueDeclare(
                    "queue.reward",
                    Boolean.TRUE,
                    Boolean.FALSE,
                    Boolean.FALSE,
                    null
            );
            //exchange和queue绑定
            channel.queueBind(
                    "queue.reward",
                    "exchange.order.reward",
                    "key.reward"
            );


            channel.basicConsume("queue.reward", Boolean.TRUE,deliverCallback,consumerTag->{});
            while(true){
                Thread.sleep(100000000);
            }
        }
    }
    DeliverCallback deliverCallback=((consumerTag,message)->{
        OrderMessageDTO messageDTO = JSON.parseObject(message.getBody(), OrderMessageDTO.class);
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        RewardPo rewardPo = new RewardPo();
        rewardPo.setAmount(messageDTO.getPrice());
        rewardPo.setDate(new Date());
        rewardPo.setOrderId(messageDTO.getOrderId());
        rewardPo.setStatus(String.valueOf(RewardStatus.SUCCESS));
        rewardDao.insert(rewardPo);
        //插入返回的id
        messageDTO.setRewardId(rewardPo.getId());
        try(Connection connection=connectionFactory.newConnection();
            Channel channel=connection.createChannel() ){
            String messageToSend = JSON.toJSONString(messageDTO);
            channel.basicPublish(
                    "exchange.order.reward",
                    "key.order",
                    null,
                    messageToSend.getBytes()
            );


        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }


    });

}
