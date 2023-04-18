package com.food;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
@MapperScan("com.food.restaurant")
public class RestaurantApplication {
    public static void main(String[] args) {

        SpringApplication.run(RestaurantApplication.class,args);
    }
}