package com.food.order.service;

import com.alibaba.fastjson.JSON;
import com.rabbitmq.client.*;
import lombok.extern.slf4j.Slf4j;
import com.food.order.dao.OrderDetailDao;
import com.food.order.dto.OrderMessageDTO;
import com.food.order.enums.OrderStatus;
import com.food.order.po.OrderDetailPO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 消息处理
 */
@Service
@Slf4j
public class OrderMessageService {
    @Autowired
    private  OrderDetailDao orderDetailDao;
    /**
     * 申明交换机、队列、绑定、消息的处理
     */
    @Async
    public void handMessage() throws IOException, TimeoutException, InterruptedException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");

       try(Connection connection = connectionFactory.newConnection();
           Channel channel = connection.createChannel()){
           channel.exchangeDeclare(
                   "exchange.order.restaurant",
                   BuiltinExchangeType.DIRECT,
                   Boolean.TRUE,
                   Boolean.FALSE,
                   null
           );
           channel.queueDeclare(
                   "queue.order",
                   Boolean.TRUE,
                   Boolean.FALSE,
                   Boolean.FALSE,
                   null

           );
           channel.queueBind(
                   "queue.order",
                   "exchange.order.restaurant",
                    "key.order"
           );
           //骑手交换机
           channel.exchangeDeclare(
                   "exchange.order.deliveryman",
                   BuiltinExchangeType.DIRECT,
                   Boolean.TRUE,
                   Boolean.FALSE,
                   null
           );
           channel.queueBind(
                   "queue.order",
                   "exchange.order.deliveryman",
                   "key.order"
           );
            channel.basicConsume("queue.order",Boolean.TRUE,deliverCallback,consumerTag->{});
            while(true){
                Thread.sleep(100000000);
            }
       }
    }
    //收到消息更新订单状态
    DeliverCallback deliverCallback=((consumerTag, message)->{
        String messageBody = new String(message.getBody());
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        try{
            OrderMessageDTO messageDTO = JSON.parseObject(messageBody, OrderMessageDTO.class);
            OrderDetailPO orderDetailPO = orderDetailDao.selectOrder(messageDTO.getOrderId());
            switch (orderDetailPO.getStatus()){
                case ORDER_CREATING:
                    if(messageDTO.getConfirmed()&&null!=messageDTO.getPrice()){
                        //商家已确认，更新订单状态
                        orderDetailPO.setStatus(OrderStatus.RESTAURANT_CONFIRMED);
                        orderDetailPO.setPrice(messageDTO.getPrice());
                        orderDetailDao.update(orderDetailPO);
                        //给骑手发送消息
                        try(Connection connection = connectionFactory.newConnection();
                            Channel channel = connection.createChannel()){
                            String messageToSend = JSON.toJSONString(messageDTO);
                            channel.basicPublish(
                                    "exchange.order.deliveryman",
                                    "key.deliveryman",
                                    null,
                                    messageToSend.getBytes()
                            );

                        }

                    }else{
                        orderDetailPO.setStatus(OrderStatus.FAILED);
                        orderDetailDao.update(orderDetailPO);
                    }
                    break;
                case RESTAURANT_CONFIRMED:
                    break;
                case DELIVERTMAN_CONFIRMED:
                    break;
                case SETTLEMENT_CONFIRMED:
                    break;
                case ORDER_CREATED:
                    break;
                case FAILED:
                    break;

            }
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }

    });

}
