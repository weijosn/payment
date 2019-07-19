package com.cdzg.money.mapper.entity;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountTitlePO {
	private long id;
	private String accountTitleName;
	private String accountTitleCode;
	private int currencyType;
	private long parentTitleId;
	private int status;
	private String accountTitleType;
	private Date insertTime;
	private Date updateTime;
	private String balanceDirection;
	private String memo;
	private int scopeType;
}
