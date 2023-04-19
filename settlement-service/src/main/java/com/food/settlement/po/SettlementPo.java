package com.food.settlement.po;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class SettlementPo {
   private Integer id;
   private Integer orderId;
   private Integer transactionId;
   private BigDecimal amount;
   private String status;
   private Date date;
}
