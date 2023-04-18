package com.food.order.po;

import lombok.Data;
import com.food.order.enums.OrderStatus;

import java.math.BigDecimal;
import java.util.Date;
@Data
public class OrderDetailPO {
    private Integer id;
    private OrderStatus status;
    private String address;
    private Integer accountId;
    private Integer productId;
    private Integer deliverymanId;
    private Integer settlementId;
    private Integer rewardId;
    private BigDecimal price;
    private Date date;
}
