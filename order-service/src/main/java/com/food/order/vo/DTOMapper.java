package com.food.order.vo;

import com.food.order.po.OrderDetailPO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DTOMapper {
    OrderDetailPO toOrderDetailPO(OrderCreateVO createVO);
}
