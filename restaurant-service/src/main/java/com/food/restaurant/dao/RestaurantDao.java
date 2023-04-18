package com.food.restaurant.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import com.food.restaurant.po.RestaurantPo;

@Mapper
public interface RestaurantDao {
    @Select("select * from restaurant where id=#{id}")
    RestaurantPo selectRestaurant(Integer id);
}
