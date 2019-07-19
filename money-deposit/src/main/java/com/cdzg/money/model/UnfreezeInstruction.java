package com.cdzg.money.model;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UnfreezeInstruction {
	/**
	 * 账户
	 */
	private Account depositAccount;
	/**
	 * 解冻业务流水号
	 */
	private String origBizNo;
	/**
	 * 解冻金额
	 */
	private BigDecimal amount;
	/**
	 * 解冻原因
	 */
	private String reason;
	/**
	 * 原冻结指令
	 */
	private FreezeInstruction origFreezeInstruction;

	/**
	 * 是否相同
	 */
	public boolean isSame(UnfreezeInstruction unfreezeInstruction) {
		if (this.origBizNo.equals(unfreezeInstruction.getOrigBizNo())
				&& this.getDepositAccount().getAccountNo()
						.equals(unfreezeInstruction.getDepositAccount().getAccountNo())
				&& this.origFreezeInstruction.getOrigBizNo()
						.equals(unfreezeInstruction.getOrigFreezeInstruction().getOrigBizNo())
				&& this.amount.compareTo(unfreezeInstruction.getAmount()) == 0) {
			return true;
		}
		return false;
	}

}
