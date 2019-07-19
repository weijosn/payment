package com.cdzg.money.api.deposit.vo.settle;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

/**
 * 无条件入款指令
 */
@Getter
@Setter
public class FundInRequest extends com.cdzg.money.api.deposit.vo.DepositBaseRequest {

	private static final long serialVersionUID = 7227705292252642412L;

	/**
	 * 入账方
	 */
	private TargetAccount account;

	/**
	 * 入款金额
	 */
	private BigDecimal amount;

	/**
	 * 套号
	 */
	private String suiteNo;

}
