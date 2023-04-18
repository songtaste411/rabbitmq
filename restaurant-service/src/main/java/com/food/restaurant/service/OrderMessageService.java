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
import java.util.concurrent.TimeoutException;

@Slf4j
@Service
public class OrderMessageService {
    @Autowired
    private ProductDao productDao;
    @Autowired
    private RestaurantDao restaurantDao;
    @Async
    public void handMessage() throws IOException, TimeoutException, InterruptedException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");


        try(Connection connection=connectionFactory.newConnection();
            Channel channel=connection.createChannel() ){
            //队列绑定关系
            channel.exchangeDeclare(
                    "exchange.order.restaurant",
                    BuiltinExchangeType.DIRECT,
                    Boolean.TRUE,
                    Boolean.FALSE,
                    null
            );
            channel.queueDeclare(
                    "queue.restaurant",
                    Boolean.TRUE,
                    Boolean.FALSE,
                    Boolean.FALSE,
                    null
            );
            channel.queueBind(
                    "queue.restaurant",
                    "exchange.order.restaurant",
                    "key.restaurant"
            );


            channel.basicConsume("queue.restaurant", Boolean.TRUE,deliverCallback,consumerTag->{});
            while(true){
                Thread.sleep(100000000);
            }
        }
    }
    DeliverCallback deliverCallback=((consumerTag,message)->{
        OrderMessageDTO messageDTO = JSON.parseObject(message.getBody(), OrderMessageDTO.class);
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        ProductPo productPo = productDao.selectProduct(messageDTO.getProductId());
        RestaurantPo restaurantPo = restaurantDao.selectRestaurant(productPo.getRestaurantId());
        if(productPo.getStatus().equals(ProductStatus.AVALIABLE.name())&& restaurantPo.getStatus().equals(RantaurantStatus.OPEN.name())){
            messageDTO.setConfirmed(Boolean.TRUE);
            messageDTO.setPrice(productPo.getPrice());
        }else{
            messageDTO.setConfirmed(Boolean.FALSE);
        }
        try(Connection connection=connectionFactory.newConnection();
            Channel channel=connection.createChannel() ){
            String messageToSend = JSON.toJSONString(messageDTO);
            channel.basicPublish(
                    "exchange.order.restaurant",
                    "key.order",
                    null,
                    messageToSend.getBytes()
            );


        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }


    });

}
