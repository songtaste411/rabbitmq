package com.food.order.vo;

import lombok.Data;

@Data
public class OrderCreateVO {
    //*用户ID
    private Integer accountId;
    //*地址
    private String address;
    //*产品ID
    private Integer productId;
}