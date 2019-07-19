package com.cdzg.money.mapper.entity;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountPO {

	private String accountId;
	private BigDecimal balance;
	private int accountType;
	private BigDecimal availableBalance;
	private int currencyType;
	private BigDecimal frozenBalance;
	private Date createTime;
	private Date lastUpdateTime;
	private String memo;
	private String accountName;
	private BigDecimal lastBalance;
	private Long accountTitleId;
	private String memberId;
	private int status;

}
