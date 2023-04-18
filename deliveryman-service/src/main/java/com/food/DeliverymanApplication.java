package com.food;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
@MapperScan("com.food.deliveryman.dao")
public class DeliverymanApplication {
    public static void main(String[] args) {

        SpringApplication.run(DeliverymanApplication.class,args);
    }
}