--创建数据库 food
create database if not exists food  DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
DROP TABLE  if exists product;
CREATE TABLE product (
id INT NOT NULL AUTO_INCREMENT COMMENT '产品id',
`name` varchar(36) DEFAULT NULL COMMENT '名称',
price DECIMAL(9,2) DEFAULT NULL COMMENT '单价',
restaurant_id int DEFAULT NULL COMMENT '餐馆id',
`status` varchar(36) DEFAULT NULL COMMENT '状态',
date datetime DEFAULT NULL COMMENT '时间',
PRIMARY KEY ( id )
)
ENGINE = INNODB AUTO_INCREMENT = 5 DEFAULT CHARSET = utf8 COMMENT='产品表';

DROP TABLE  if exists restaurant;
CREATE TABLE restaurant (
id INT NOT NULL AUTO_INCREMENT COMMENT '餐馆id',
`name` varchar(36) DEFAULT NULL COMMENT '名称',
address varchar(36) DEFAULT NULL COMMENT '地址',
`status` varchar(36) DEFAULT NULL COMMENT '状态',
date datetime DEFAULT NULL COMMENT '时间',
PRIMARY KEY ( id )
)
ENGINE = INNODB  DEFAULT CHARSET = utf8 COMMENT='餐馆表';

DROP TABLE  if exists order_detail;
CREATE TABLE order_detail (
id INT NOT NULL AUTO_INCREMENT COMMENT '订单id',
account_id int DEFAULT NULL COMMENT '账号id',
product_id int DEFAULT NULL COMMENT '产品id',
deliveryman_id int DEFAULT NULL COMMENT '外卖员id',
settlement_id int DEFAULT NULL COMMENT '结算id',
reward_id int DEFAULT NULL COMMENT '积分结算id',
price DECIMAL(9,2) DEFAULT NULL COMMENT '单价',
address varchar(36) DEFAULT NULL COMMENT '地址',
`status` varchar(36) DEFAULT NULL COMMENT '状态',
date datetime DEFAULT NULL COMMENT '时间',
PRIMARY KEY ( id )
)
ENGINE = INNODB  DEFAULT CHARSET = utf8 COMMENT='订单表';

DROP TABLE  if exists settlement;
CREATE TABLE settlement (
id INT NOT NULL AUTO_INCREMENT COMMENT '结算id',
order_id int DEFAULT NULL COMMENT '订单id',
transaction_id int DEFAULT NULL COMMENT '流水号',
amount DECIMAL(9,2) DEFAULT NULL COMMENT '金额',
`status` varchar(36) DEFAULT NULL COMMENT '状态',
date datetime DEFAULT NULL COMMENT '时间',
PRIMARY KEY ( id )
)
ENGINE = INNODB  DEFAULT CHARSET = utf8 COMMENT='结算表';