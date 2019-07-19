package com.cdzg.money.api.deposit.vo.settle;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UnfreezeAccounting extends SettlementInstruction {

	private static final long serialVersionUID = -5121899638233598911L;

	private TargetAccount targetAccount;

	/**
	 * 原始冻结指令ID
	 */
	private String origBizNo;

}
