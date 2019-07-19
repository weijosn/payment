package com.cdzg.money.mapper.entity;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountFrozenPO {

	private long id;

	private String accountId;

	private BigDecimal amount;
	/**
	 * varchar 必填<br>
	 * 原始订单号
	 */
	private String origBizNo;

	private BigDecimal availBalance;

	/**
	 * DECIMAL(22) 必填<br>
	 * 记录动作类型：1：冻结 2:解冻
	 */
	private int freezeType;

	private Date insertDate;

	/**
	 * DECIMAL(22) 必填<br>
	 * 冻结状态：1:冻结中，2:已解冻
	 */
	private int status;

	/**
	 * DECIMAL(19,4)<br>
	 * 可解冻金额
	 */
	private BigDecimal availUnfrozenAmount;
	
	private String reason;
	
}