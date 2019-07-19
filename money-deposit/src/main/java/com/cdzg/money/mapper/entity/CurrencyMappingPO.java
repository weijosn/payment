package com.cdzg.money.mapper.entity;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CurrencyMappingPO {
	private long id;
	private String currencyCode;
	private int currencyType;
	private String currencyName;
	private Date createTime;
}
