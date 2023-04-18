package com.food.order.service;

import com.alibaba.fastjson.JSON;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.food.order.dao.OrderDetailDao;
import com.food.order.dto.OrderMessageDTO;
import com.food.order.enums.OrderStatus;
import com.food.order.vo.DTOMapper;
import com.food.order.po.OrderDetailPO;
import com.food.order.vo.OrderCreateVO;
import lombok.extern.slf4j.Slf4j;
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
    public void publicMessage(OrderMessageDTO messageDTO) throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        try (Connection connection = connectionFactory.newConnection();
             Channel channel=connection.createChannel()) {
            String messageToSend = JSON.toJSONString(messageDTO);
            channel.basicPublish(
                "exchange.order.restaurant",
                    "key.restaurant",
                    null,
                    messageToSend.getBytes()
            );

        }
    }
}
