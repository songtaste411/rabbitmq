package com.food.restaurant.po;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
@Data
public class ProductPo {
   private Integer id;
   private String name;
   private BigDecimal price;
   private Integer restaurantId;
   private String status;
   private Date date;
}
