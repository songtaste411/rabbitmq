package com.food.restaurant.config;

import com.food.restaurant.service.OrderMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Configuration
public class RabbitConfig {
    @Autowired
    private OrderMessageService orderMessageService;
    @Autowired
    public void startListenMessage() throws IOException, InterruptedException, TimeoutException {
        orderMessageService.handMessage();
    }
}
