package com.cdzg.money.model;

import java.io.Serializable;
import java.math.BigDecimal;

import com.cdzg.money.api.deposit.vo.account.AccountStatusEnum;
import com.cdzg.money.api.deposit.vo.account.AccountTypeEnum;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Account implements Serializable {

	private static final long serialVersionUID = 1155374882325258215L;

	private String accountNo;

	private AccountTypeEnum accountType;

	private AccountStatusEnum status;

	private Currency currency;

	private AcctTitle acctTitle;

	private String memo;

	private String memberId;

	private String accountName;

	private BigDecimal balance;

	private BigDecimal availablebalance;

	private BigDecimal frozenbalance;

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 79 * hash + (this.accountNo != null ? this.accountNo.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Account other = (Account) obj;
		// 如果两者的accountNo 一致，则认为相等
		if ((this.accountNo == null) ? (other.accountNo != null) : !this.accountNo.equals(other.accountNo)) {
			return false;
		}

		return true;
	}

}
