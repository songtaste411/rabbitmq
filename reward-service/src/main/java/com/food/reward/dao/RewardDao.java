package com.food.reward.dao;

import com.food.reward.po.RewardPo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

@Mapper
public interface RewardDao {
    @Insert("INSERT INTO reward (order_id, amount, status, date) VALUES (#{orderId},#{amount},#{status},#{date})")
    @Options (useGeneratedKeys = true, keyProperty = "id")
    void insert(RewardPo rewardPO);
}
