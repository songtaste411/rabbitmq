package com.food.settlement.service;

import com.alibaba.fastjson.JSON;
import com.food.settlement.dao.SettlementDao;
import com.food.settlement.dto.OrderMessageDTO;
import com.food.settlement.enums.ProductStatus;
import com.food.settlement.enums.SettlementStatus;
import com.food.settlement.po.SettlementPo;
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
    private SettlementService settlementService;
    @Autowired
    private SettlementDao settlementDao;
    @Async
    public void handMessage() throws IOException, TimeoutException, InterruptedException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");


        try(Connection connection=connectionFactory.newConnection();
            Channel channel=connection.createChannel() ){
            //监听的exchange
            channel.exchangeDeclare(
                    "exchange.order.settlement",
                    BuiltinExchangeType.FANOUT,
                    Boolean.TRUE,
                    Boolean.FALSE,
                    null
            );
            //监听的queue
            channel.queueDeclare(
                    "queue.settlement",
                    Boolean.TRUE,
                    Boolean.FALSE,
                    Boolean.FALSE,
                    null
            );
            //exchange和queue绑定
            channel.queueBind(
                    "queue.settlement",
                    "exchange.order.settlement",
                    "key.settlement"
            );


            channel.basicConsume("queue.settlement", Boolean.TRUE,deliverCallback,consumerTag->{});
            while(true){
                Thread.sleep(100000000);
            }
        }
    }
    DeliverCallback deliverCallback=((consumerTag,message)->{
        OrderMessageDTO messageDTO = JSON.parseObject(message.getBody(), OrderMessageDTO.class);
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        SettlementPo settlementPo = new SettlementPo();
        settlementPo.setAmount(messageDTO.getPrice());
        settlementPo.setDate(new Date());
        settlementPo.setOrderId(messageDTO.getOrderId());
        Integer tranid = settlementService.settlement(messageDTO.getAccountId(), messageDTO.getPrice());
        settlementPo.setTransactionId(tranid);
        settlementPo.setStatus(String.valueOf(SettlementStatus.SUCCESS));
        settlementDao.insert(settlementPo);

        try(Connection connection=connectionFactory.newConnection();
            Channel channel=connection.createChannel() ){
            String messageToSend = JSON.toJSONString(messageDTO);
            channel.basicPublish(
                    "exchange.settlement.order",
                    "key.order",
                    null,
                    messageToSend.getBytes()
            );


        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }


    });

}
