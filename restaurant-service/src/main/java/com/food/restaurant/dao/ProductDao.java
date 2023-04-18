package com.food.restaurant.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import com.food.restaurant.po.ProductPo;

@Mapper
public interface ProductDao {
    @Select("select * from product where id=#{id}")
    ProductPo selectProduct(Integer id);
}
