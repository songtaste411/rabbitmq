package com.food;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
@MapperScan("com.food.settlement.dao")
public class SettlementApplication {
    public static void main(String[] args) {

        SpringApplication.run(SettlementApplication.class,args);
    }
}