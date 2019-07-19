CREATE TABLE `tb_cluster_lock` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `lock_name` varchar(100) DEFAULT NULL COMMENT '锁定名称',
  `task_name` varchar(100) DEFAULT NULL COMMENT '任务名称',
  `lock_status` int(11) DEFAULT NULL COMMENT '是否锁定',
  `lock_time` datetime DEFAULT NULL COMMENT '锁定时间',
  `over_second` int(11) DEFAULT NULL COMMENT '超时时间(秒)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

CREATE TABLE `tb_notify` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `payment_order_no` varchar(45) DEFAULT NULL COMMENT '支付订单号',
  `notify_url` varchar(256) DEFAULT NULL COMMENT '支付回调地址',
  `notify_status` int(11) DEFAULT NULL COMMENT '通知状态',
  `goods_name` varchar(45) DEFAULT NULL COMMENT '商品名称',
  `response_code` int(11) DEFAULT NULL COMMENT '响应结果',
  `notify_time` datetime DEFAULT NULL COMMENT '通知时间',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `notify_times` int(11) DEFAULT NULL COMMENT '通知次数',
  PRIMARY KEY (`id`),
  KEY `IDX_ORDER_NO` (`payment_order_no`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8;

CREATE TABLE `tb_payment_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `payment_order_id` varchar(32) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '支付订单号',
  `order_amount` decimal(10,2) DEFAULT NULL COMMENT '支付金额',
  `paied_amount` decimal(10,2) DEFAULT NULL COMMENT '实付金额',
  `payment_code` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '支付产品码',
  `order_status` varchar(10) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '支付状态',
  `payment_time` datetime DEFAULT NULL COMMENT '实际支付时间',
  `readly_time` datetime DEFAULT NULL COMMENT '准备支付时间',
  `create_time` datetime DEFAULT NULL COMMENT '订单创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '订单修改时间',
  `app_id` varchar(45) COLLATE utf8_unicode_ci DEFAULT '' COMMENT '请求来源',
  `trade_no` varchar(45) COLLATE utf8_unicode_ci DEFAULT '' COMMENT '请求来源交易号',
  `order_type` varchar(45) COLLATE utf8_unicode_ci DEFAULT '' COMMENT '订单类型',
  `remark` varchar(128) COLLATE utf8_unicode_ci DEFAULT '' COMMENT '请求来源备注',
  PRIMARY KEY (`id`),
  KEY `IDX_ORDER` (`payment_order_id`)
) ENGINE=InnoDB AUTO_INCREMENT=59 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `tb_payment_request` (
  `id` bigint(11) unsigned NOT NULL AUTO_INCREMENT,
  `payment_code` varchar(20) COLLATE utf8_unicode_ci DEFAULT '' COMMENT '产品编码',
  `payment_order_id` varchar(32) COLLATE utf8_unicode_ci DEFAULT '' COMMENT '支付订单号',
  `payment_version` varchar(10) COLLATE utf8_unicode_ci DEFAULT '',
  `create_time` datetime DEFAULT NULL,
  `biz_no` varchar(45) COLLATE utf8_unicode_ci DEFAULT '' COMMENT '请求方业务编码',
  `remark` varchar(128) COLLATE utf8_unicode_ci DEFAULT '',
  `app_id` varchar(45) COLLATE utf8_unicode_ci DEFAULT '' COMMENT '请求方ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_BIZNO` (`biz_no`,`app_id`),
  KEY `IDX_ORDER` (`payment_order_id`)
) ENGINE=InnoDB AUTO_INCREMENT=64 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `tb_payment_stage` (
  `id` bigint(11) unsigned NOT NULL AUTO_INCREMENT,
  `payment_order_id` varchar(32) COLLATE utf8_unicode_ci DEFAULT '',
  `pay_type` varchar(45) COLLATE utf8_unicode_ci DEFAULT '',
  `pay_plat` varchar(45) COLLATE utf8_unicode_ci DEFAULT '',
  `fee_amount` decimal(10,2) DEFAULT '0.00',
  `order_amount` decimal(10,2) DEFAULT '0.00',
  `paied_amount` decimal(10,2) DEFAULT '0.00',
  `payment_time` datetime DEFAULT NULL,
  `readly_time` datetime DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `summary` varchar(128) COLLATE utf8_unicode_ci DEFAULT '',
  `payee_account_id` varchar(45) COLLATE utf8_unicode_ci DEFAULT '',
  `payer_account_id` varchar(45) COLLATE utf8_unicode_ci DEFAULT '',
  `order_status` varchar(10) COLLATE utf8_unicode_ci DEFAULT '',
  PRIMARY KEY (`id`),
  KEY `IDX_ORDER` (`payment_order_id`)
) ENGINE=InnoDB AUTO_INCREMENT=101 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `tb_refund_order` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `payment_order_id` varchar(32) COLLATE utf8_unicode_ci DEFAULT '' COMMENT '支付业务订单号',
  `refund_order_id` varchar(32) COLLATE utf8_unicode_ci DEFAULT '' COMMENT '退款订单号',
  `refund_amount` decimal(10,2) DEFAULT NULL COMMENT '退款金额',
  `actual_refund_amount` decimal(10,2) DEFAULT NULL COMMENT '实退金额',
  `refund_status` varchar(45) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '退款状态\n	INIT("I", "待退款"),\n\n	PENDDING("P", "退款进行中"),\n\n	SUCCESS("PS", "退款成功"),\n\n	FAILED("F", "退款失败");',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `refund_time` datetime DEFAULT NULL COMMENT '退款时间',
  `realy_time` datetime DEFAULT NULL COMMENT '发起时间',
  `trade_no` varchar(32) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '交易流水',
  `app_id` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '请求来源',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_ORDERNO` (`payment_order_id`),
  KEY `IDX_ORDER` (`app_id`,`trade_no`),
  KEY `IDX_TIME` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `tb_sequence` (
  `sequence_name` varchar(64) NOT NULL COMMENT '序列名称',
  `value` int(11) DEFAULT NULL COMMENT '当前值',
  PRIMARY KEY (`sequence_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;


DELIMITER $$
CREATE DEFINER=`root`@`localhost` FUNCTION `nextval`(sequence_name varchar(64)) RETURNS int(11)
BEGIN
    declare current integer;
    set current = 0;

		INSERT INTO tb_sequence(sequence_name,value)  
		SELECT  sequence_name,1 from tb_sequence
		WHERE not EXISTS(  
					SELECT 1 from tb_sequence t where t.sequence_name = sequence_name
		) limit 1;

    update tb_sequence t set t.value = t.value + 1 where t.sequence_name = sequence_name;
    select t.value into current from tb_sequence t where t.sequence_name = sequence_name;

    return current;
end$$
DELIMITER ;


INSERT INTO tb_sequence(sequence_name,value)  values('1','1')
