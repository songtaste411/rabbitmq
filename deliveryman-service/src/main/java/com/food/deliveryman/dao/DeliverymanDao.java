package com.food.deliveryman.dao;

import org.apache.ibatis.annotations.Mapper;
import com.food.deliveryman.enums.DeliverymanStatus;
import com.food.deliveryman.po.DeliverymanPo;

import java.util.List;
@Mapper
public interface DeliverymanDao {

    List<DeliverymanPo> selectDeliverymanBystatus(DeliverymanStatus status);
}
