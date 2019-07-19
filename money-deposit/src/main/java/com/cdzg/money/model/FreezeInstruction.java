package com.cdzg.money.model;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FreezeInstruction {
	/**
	 * 账户
	 */
	private Account depositAccount;
	/**
	 * 冻结业务流水号
	 */
	private String origBizNo;
	/**
	 * 冻结金额
	 */
	private BigDecimal amount;
	/**
	 * 冻结原因
	 */
	private String reason;
	/**
	 * 可解冻金额
	 */
	private BigDecimal availabeUnfrozenAmount;

	/**
	 * 是否相同
	 */
	public boolean isSame(FreezeInstruction FreezeInstruction) {
		if (FreezeInstruction == null) {
			return false;
		}
		if (this.origBizNo.equals(FreezeInstruction.getOrigBizNo())
				&& this.getDepositAccount().getAccountNo().equals(FreezeInstruction.getDepositAccount().getAccountNo())
				&& this.amount.compareTo(FreezeInstruction.getAmount()) == 0) {
			return true;
		}
		return false;
	}

}
