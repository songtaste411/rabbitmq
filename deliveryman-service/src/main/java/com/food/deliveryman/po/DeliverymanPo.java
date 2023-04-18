package com.food.deliveryman.po;

import lombok.Data;
import com.food.deliveryman.enums.DeliverymanStatus;

import java.util.Date;
@Data
public class DeliverymanPo {
    private Integer id;
    private String name;
    private String district;
    private DeliverymanStatus status;
    private Date date;
}
