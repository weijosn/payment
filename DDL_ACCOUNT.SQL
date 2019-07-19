/*
-- Query: 表结构语句
*/
CREATE TABLE `tb_account` (
  `account_id` varchar(32) NOT NULL DEFAULT '',
  `balance` decimal(10,2) DEFAULT NULL,
  `account_type` int(10) DEFAULT NULL,
  `available_balance` decimal(10,2) DEFAULT NULL,
  `currency_type` int(10) DEFAULT NULL,
  `frozen_balance` decimal(10,2) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `last_update_time` datetime DEFAULT NULL,
  `memo` varchar(128) DEFAULT NULL,
  `account_name` varchar(32) DEFAULT NULL,
  `last_balance` decimal(10,2) DEFAULT NULL,
  `account_title_id` bigint(20) DEFAULT NULL,
  `member_id` varchar(32) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  PRIMARY KEY (`account_id`),
  KEY `IDX_ACCOUNT` (`account_id`),
  KEY `IDX_MEMBER` (`member_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `tb_account_dc` (
  `id` bigint(11) unsigned NOT NULL AUTO_INCREMENT,
  `account_id` varchar(32) DEFAULT NULL,
  `account_title_id` bigint(11) DEFAULT NULL,
  `dc_direction` varchar(10) DEFAULT NULL,
  `amount` decimal(10,2) DEFAULT NULL,
  `balance` decimal(10,2) DEFAULT NULL,
  `insert_time` datetime DEFAULT NULL,
  `currency_type` int(10) DEFAULT NULL,
  `summary` varchar(64) DEFAULT NULL,
  `settle_time` datetime DEFAULT NULL,
  `suite_no` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `IDX_ACCOUNT` (`account_id`)
) ENGINE=InnoDB AUTO_INCREMENT=399 DEFAULT CHARSET=utf8;

CREATE TABLE `tb_account_frozen` (
  `id` bigint(11) unsigned NOT NULL AUTO_INCREMENT,
  `account_id` varchar(32) DEFAULT NULL,
  `amount` decimal(11,2) DEFAULT NULL,
  `orig_biz_no` varchar(32) DEFAULT NULL,
  `reason` varchar(64) DEFAULT NULL,
  `avail_balance` decimal(11,2) DEFAULT NULL,
  `freeze_type` int(11) DEFAULT NULL,
  `insert_date` datetime DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `avail_unfrozen_amount` decimal(11,2) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_account_id` (`account_id`)
) ENGINE=InnoDB AUTO_INCREMENT=112 DEFAULT CHARSET=utf8;

CREATE TABLE `tb_account_state_log` (
  `id` bigint(11) unsigned NOT NULL AUTO_INCREMENT,
  `member_id` varchar(32) DEFAULT NULL,
  `app_id` varchar(20) DEFAULT NULL,
  `action` varchar(20) DEFAULT NULL,
  `insert_time` datetime DEFAULT NULL,
  `request_no` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `tb_account_title` (
  `id` bigint(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增主健',
  `account_title_name` varchar(32) DEFAULT NULL,
  `account_title_code` varchar(20) DEFAULT NULL,
  `currency_type` int(20) DEFAULT NULL,
  `parent_title_id` bigint(20) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `account_title_type` varchar(20) DEFAULT NULL,
  `insert_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `balance_direction` varchar(10) DEFAULT NULL,
  `memo` varchar(128) DEFAULT NULL,
  `scopeType` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_CODE` (`account_title_code`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

CREATE TABLE `tb_currency_mapping` (
  `id` bigint(11) unsigned NOT NULL AUTO_INCREMENT,
  `currency_code` varchar(32) DEFAULT NULL,
  `currency_name` varchar(32) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `currency_type` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

CREATE TABLE `tb_sequence` (
  `sequence_name` varchar(64) NOT NULL COMMENT '序列名称',
  `value` int(11) DEFAULT NULL COMMENT '当前值',
  PRIMARY KEY (`sequence_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

CREATE TABLE `tb_transaction` (
  `id` bigint(11) unsigned NOT NULL AUTO_INCREMENT,
  `biz_no` varchar(32) DEFAULT NULL,
  `insert_time` datetime DEFAULT NULL,
  `app_id` varchar(32) DEFAULT NULL,
  `status` int(10) DEFAULT NULL,
  `memo` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_BIZNO` (`biz_no`,`app_id`)
) ENGINE=InnoDB AUTO_INCREMENT=401 DEFAULT CHARSET=utf8;

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



/*
-- Query: SELECT * FROM `money-deposit`.tb_account_title
*/
INSERT INTO `tb_account_title` (`id`,`account_title_name`,`account_title_code`,`currency_type`,`parent_title_id`,`status`,`account_title_type`,`insert_time`,`update_time`,`balance_direction`,`memo`,`scopeType`) VALUES (1,'广发资金','101',1,0,1,'0','2018-09-09 00:00:00','2018-09-09 00:00:00','I','无',2);
INSERT INTO `tb_account_title` (`id`,`account_title_name`,`account_title_code`,`currency_type`,`parent_title_id`,`status`,`account_title_type`,`insert_time`,`update_time`,`balance_direction`,`memo`,`scopeType`) VALUES (2,'通联资金','102',1,0,1,'0',NULL,NULL,'I',NULL,2);
INSERT INTO `tb_account_title` (`id`,`account_title_name`,`account_title_code`,`currency_type`,`parent_title_id`,`status`,`account_title_type`,`insert_time`,`update_time`,`balance_direction`,`memo`,`scopeType`) VALUES (3,'中金资金','103',1,0,1,'0',NULL,NULL,'I','无',2);


INSERT INTO tb_sequence(sequence_name,value)  values('1','1')

