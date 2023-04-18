package com.food.restaurant.po;

import lombok.Data;
import com.food.restaurant.enums.RantaurantStatus;

import java.util.Date;
@Data
public class RestaurantPo {

    private Integer id;
    private String name;
    private String address;
    private String status;
    private Date date;
}
