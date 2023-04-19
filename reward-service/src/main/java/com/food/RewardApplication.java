package com.food;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
@MapperScan("com.food.reward.dao")
public class RewardApplication {
    public static void main(String[] args) {

        SpringApplication.run(RewardApplication.class,args);
    }
}