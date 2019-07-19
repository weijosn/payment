package com.cdzg.money.mapper.entity;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountDcPO {

	private long id;
	private String accountId;
	private long accountTitleId;
	private String dcDirection;

	private BigDecimal amount;
	private BigDecimal balance;

	private Date insertTime;
	private int currencyType;

	private String suiteNo;

	private String summary;

	private Date settleTime;
}
