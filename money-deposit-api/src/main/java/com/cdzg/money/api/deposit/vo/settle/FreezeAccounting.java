package com.cdzg.money.api.deposit.vo.settle;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FreezeAccounting extends SettlementInstruction {

	private static final long serialVersionUID = 6467404417715193561L;

	private TargetAccount targetAccount;

	/**
	 * 冻结业务号，解冻时需要传
	 */
	private String origBizNo;

}
