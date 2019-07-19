package com.cdzg.money.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Currency implements Serializable {

	private static final long serialVersionUID = -7587284982561778673L;

	/**
	 * 标准币种，譬如"CNY", "USD"等
	 */
	private String currencyCode;
	/**
	 * 库表中对应的币种的数字 货币类型 1 RMB 2 美金 3 港币 4 欧元 5 澳元 6 台币 7 日元 8 韩元 9 新加坡币
	 */
	private int currencyType;
	
	private String currencyName;

}
