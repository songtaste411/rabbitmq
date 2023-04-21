package com.food.order.service;

import com.alibaba.fastjson.JSON;
import com.rabbitmq.client.*;
import com.food.order.dao.OrderDetailDao;
import com.food.order.dto.OrderMessageDTO;
import com.food.order.enums.OrderStatus;
import com.food.order.vo.DTOMapper;
import com.food.order.po.OrderDetailPO;
import com.food.order.vo.OrderCreateVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeoutException;
@Slf4j
@Service
public class OrderService {
    @Autowired
    private DTOMapper dtoMapper;
    @Autowired
    private OrderDetailDao orderDetailDao;
    @Autowired
    private RabbitTemplate rabbitTemplate;


    public void createOrder(OrderCreateVO createVO) {
        OrderDetailPO po= dtoMapper.toOrderDetailPO(createVO);
        po.setDate(new Date());
        po.setStatus(OrderStatus.ORDER_CREATING);
        orderDetailDao.insert(po);

        OrderMessageDTO messageDTO = new OrderMessageDTO();
        messageDTO.setOrderId(po.getId());
        messageDTO.setAccountId(po.getAccountId());
        messageDTO.setProductId(po.getProductId());
        //发送消息
        try {
            publicMessage(messageDTO);
        } catch (Exception e) {
            log.error("消息发送失败：{}",e.getMessage());
        }

    }

    /**
     * 发送消息
     * @param messageDTO
     * @throws IOException
     * @throws TimeoutException
     */
    public void publicMessage(OrderMessageDTO messageDTO) throws IOException, TimeoutException, InterruptedException {

        String messageToSend = JSON.toJSONString(messageDTO);
        Message message = new Message(messageToSend.getBytes());
        CorrelationData correlationData=new CorrelationData();
        correlationData.setId(messageDTO.getOrderId().toString());
        rabbitTemplate.send( "exchange.order.restaurant","key.restaurant",message,correlationData);
        log.info("消息开始发送");
//        ConnectionFactory connectionFactory = new ConnectionFactory();
//        connectionFactory.setHost("localhost");
//        try (Connection connection = connectionFactory.newConnection();
//             Channel channel=connection.createChannel()) {
//            //开启消息确认机制
//            channel.confirmSelect();
//            String messageToSend = JSON.toJSONString(messageDTO);
//            //设置15秒消息过期时间
//            AMQP.BasicProperties basicProperties = new AMQP.BasicProperties().builder().expiration("15000").build();
//            channel.basicPublish(
//                    "exchange.order.restaurant",
//                    "key.restaurant",
//                    null,
//                    messageToSend.getBytes()
//            );
//            log.info("消息开始发送");
//            if(channel.waitForConfirms()){
//                log.info("消息确认成功");
//            }else{
//                log.error("消息确认失败");
//            }
            //演示异步确认方法
//            ConfirmListener confirmListener = new ConfirmListener() {
//                @Override
//                public void handleAck(long deliveryTag, boolean multiple) throws IOException {
//                    log. info("Ack,deiveryTag:{}, mutiple:{}", deliveryTag, multiple);
//                }
//
//                @Override
//                public void handleNack(long deliveryTag, boolean multiple) throws IOException {
//                    log. error("Nack,deiveryTag:{}, mutiple:{}", deliveryTag, multiple);
//                }
//            };
//            channel.addConfirmListener(confirmListener);
            //演示多条同步确认-不推荐使用-缺点消息确认成功但不确定是否全部成功
//            for(int i=0;i<50;i++){
//                channel.basicPublish(
//                        "exchange.order.restaurant",
//                        "key.restaurant",
//                        null,
//                        messageToSend.getBytes()
//                );
//                log.info("消息开始发送");
//            }
            Thread.sleep(1000);


        }
    }
