package com.food.order.controller;

import lombok.extern.slf4j.Slf4j;
import com.food.order.service.OrderService;
import com.food.order.vo.OrderCreateVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @PostMapping("/create")
    public void createOrder(@RequestBody OrderCreateVO createVO){
        log.info("订单创建了：OrderCreateVO:{}",createVO);
        orderService.createOrder(createVO);
    }
}
