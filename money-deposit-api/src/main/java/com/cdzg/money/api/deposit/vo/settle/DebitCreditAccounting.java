package com.cdzg.money.api.deposit.vo.settle;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DebitCreditAccounting extends SettlementInstruction {

	private static final long serialVersionUID = -3984344768195664698L;

	/**
	 * 借方
	 */
	private TargetAccount debitAccount;

	/**
	 * 贷方
	 */
	private TargetAccount creditAccount;

	/**
	 * 套号
	 */
	private String suiteNo;

}
