package com.food.order.service;

import com.alibaba.fastjson.JSON;
import com.rabbitmq.client.*;
import lombok.extern.slf4j.Slf4j;
import com.food.order.dao.OrderDetailDao;
import com.food.order.dto.OrderMessageDTO;
import com.food.order.enums.OrderStatus;
import com.food.order.po.OrderDetailPO;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeoutException;

/**
 * 消息处理
 */
@Service
@Slf4j
public class OrderMessageService {
    @Autowired
    private  OrderDetailDao orderDetailDao;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    //收到消息更新订单状态
    @RabbitListener(
            bindings = {
            @QueueBinding(value = @Queue(name = "queue.order"), exchange = @Exchange(name = "exchange.order.restaurant"),key = "key.order"),
            @QueueBinding(value = @Queue(name = "queue.order"), exchange = @Exchange(name = "exchange.order.deliveryman"),key = "key.order"),
            @QueueBinding(value = @Queue(name = "queue.order"), exchange = @Exchange(name = "exchange.settlement.order",type = ExchangeTypes.FANOUT),key = "key.order"),
            @QueueBinding(value = @Queue(name = "queue.order"), exchange = @Exchange(name = "exchange.order.reward",type = ExchangeTypes.TOPIC),key = "key.order")
    })
    public void handleMessage(@Payload Message message) {

        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        try {
            OrderMessageDTO messageDTO = JSON.parseObject(message.getBody(), OrderMessageDTO.class);
            OrderDetailPO orderDetailPO = orderDetailDao.selectOrder(messageDTO.getOrderId());
            switch (orderDetailPO.getStatus()) {
                case ORDER_CREATING:
                    if (messageDTO.getConfirmed() && null != messageDTO.getPrice()) {
                        //商家已确认，更新订单状态
                        orderDetailPO.setStatus(OrderStatus.RESTAURANT_CONFIRMED);
                        orderDetailPO.setPrice(messageDTO.getPrice());
                        orderDetailDao.update(orderDetailPO);
                        //给骑手发送消息
                        rabbitTemplate.send("exchange.order.deliveryman",
                                "key.deliveryman",message,null);

                    } else {
                        orderDetailPO.setStatus(OrderStatus.FAILED);
                        orderDetailDao.update(orderDetailPO);
                    }
                    break;
                case RESTAURANT_CONFIRMED:
                    if (null != messageDTO.getDeliverymanId()) {
                        orderDetailPO.setStatus(OrderStatus.DELIVERTMAN_CONFIRMED);
                        orderDetailPO.setDeliverymanId(messageDTO.getDeliverymanId());
                        orderDetailDao.update(orderDetailPO);
                        //给结算服务发送消息
                        rabbitTemplate.send("exchange.order.settlement",
                                "key.settlement",message,null);
                    } else {
                        orderDetailPO.setStatus(OrderStatus.FAILED);
                        orderDetailDao.update(orderDetailPO);
                    }
                    break;
                case DELIVERTMAN_CONFIRMED:
                    if (null != messageDTO.getSettlementId()) {
                        orderDetailPO.setStatus(OrderStatus.SETTLEMENT_CONFIRMED);
                        orderDetailPO.setSettlementId(messageDTO.getSettlementId());
                        orderDetailDao.update(orderDetailPO);
                        //给积分服务发送消息
                        rabbitTemplate.send("exchange.order.reward",
                                "key.reward",message,null);
                    } else {
                        orderDetailPO.setStatus(OrderStatus.FAILED);
                        orderDetailDao.update(orderDetailPO);
                    }
                    break;
                case SETTLEMENT_CONFIRMED:
                    if (null != messageDTO.getRewardId()) {
                        orderDetailPO.setStatus(OrderStatus.ORDER_CREATED);
                        orderDetailPO.setRewardId(messageDTO.getRewardId());
                        orderDetailDao.update(orderDetailPO);
                    } else {
                        orderDetailPO.setStatus(OrderStatus.FAILED);
                        orderDetailDao.update(orderDetailPO);
                    }
                    break;
                case ORDER_CREATED:
                    break;
                case FAILED:
                    break;

            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    };

}
