package com.cdzg.money.api.deposit.vo.settle;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class SettlementInstruction implements java.io.Serializable{

	private static final long serialVersionUID = 6195151754549474801L;

	private String currencyCode = "CNY";

	/**
	 * 金额。必须大于0，最多两位小数。
	 */
	private BigDecimal amount;

	/**
	 * 指令目的，来源等说明
	 */
	private String summary;

	/**
	 * 结算时间
	 */
	private Date settlementTime;

}
