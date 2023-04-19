package com.food.settlement.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Random;

@Service
public class SettlementService {
    Random random = new Random(25);
    public Integer settlement(Integer accountId, BigDecimal amount){
        //这里实际要实现真正的结算业务代码
        //根据业务需要
        return random.nextInt(100000000);
    }

}
