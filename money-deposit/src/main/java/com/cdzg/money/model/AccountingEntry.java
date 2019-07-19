package com.cdzg.money.model;

import java.math.BigDecimal;

import com.cdzg.money.api.deposit.vo.account.AccountStatusEnum;
import com.cdzg.money.exception.DepositResultCode;
import com.cdzg.money.exception.DepositException;
import com.cdzg.money.model.enums.DrCr;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountingEntry {
	/**
	 * 账户信息
	 */
	private Account depositAccount;
	/**
	 * 交易金额
	 */
	private BigDecimal amount;
	/**
	 * 借贷
	 */
	private DrCr drCr;
	/**
	 * 是否需要缓冲入账
	 */
	private boolean needsBufferAccouting = false;
	/**
	 * 备注
	 */
	private String memo;

	public void validateAccountStatus() {
		AccountStatusEnum status = this.depositAccount.getStatus();
		if (!AccountStatusEnum.Active.equals(status) && !AccountStatusEnum.FundOutRefused.equals(status)
				&& !AccountStatusEnum.FundInRefused.equals(status)) {
			throw new DepositException(DepositResultCode.ACCOUNT_STATUS_MISMATCH, "the accountId["
					+ this.getDepositAccount().getAccountNo() + "]'s accountStatus is not allowed to accounting");
		}
	}

}
