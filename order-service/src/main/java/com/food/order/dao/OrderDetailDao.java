package com.food.order.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.food.order.po.OrderDetailPO;
import org.apache.ibatis.annotations.*;

@Mapper
public interface OrderDetailDao{
    @Insert("INSERT INTO order_detail(status, address ,account_id, product_id ,deliveryman_id, settlement_id, reward_id, price, date)"
            +"VALUES(#{status},#{address}, #{accountId},#{productId}, #{deliverymanId},#{settlementId},#{rewardId}, #{price}, #{date})")
    @Options(useGeneratedKeys = true,keyProperty = "id")
    int insert(OrderDetailPO orderDetailPO);
    @Update("UPDATE order_detail SET status=#{status}, address=#{address},account_id=#{accountId}, product_id =#{productId},"
            +"deliveryman_id =#{deliverymanId}, settlement_id =#{settlementId},reward_id =#{rewardId}, price =#{price}, date =#{date} where id=#{id}")
    void update(OrderDetailPO orderDetailPO);
    @Select("select * from order_detail where id=#{id}")
    OrderDetailPO selectOrder(Integer id);
}
