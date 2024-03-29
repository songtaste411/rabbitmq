package com.food.settlement.dao;

import com.food.settlement.po.SettlementPo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

@Mapper
public interface SettlementDao {
    @Insert("INSERT INTO settlement (order_id, transaction_id, status,amount, date) VALUES(#{orderId} ,#{transactionId} ,#{status},#{amount},#{date})")
    @Options(useGeneratedKeys = true,keyProperty = "id")
    void insert(SettlementPo settlementPO);
}
