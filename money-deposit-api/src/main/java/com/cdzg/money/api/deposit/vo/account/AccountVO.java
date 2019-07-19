package com.cdzg.money.api.deposit.vo.account;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountVO implements java.io.Serializable{

	private static final long serialVersionUID = -8586276797665060731L;

	private String memberId;

	private String accountNo;

	/**
	 * 0:未激活 1:激活 2:锁定 3:止出 4:止入 5:注销
	 */
	private AccountStatusEnum status;

	/**
	 * 标准币种，譬如"CNY", "USD"等
	 */
	private String currencyCode = "CNY";

	/**
	 * 账户类型 1:对私 2:对公 3:对内 4:卡账户
	 */
	private AccountTypeEnum accountType;

	/**
	 * 余额
	 */
	private Balance balance;

	/**
	 * 账户名称
	 */
	private String accountName;

	@Getter
	@Setter
	public static class Balance implements java.io.Serializable{

		private static final long serialVersionUID = 6221689331631377495L;

		public Balance(BigDecimal balance, BigDecimal availableBalance, BigDecimal frozenBalance) {
			this.balance = balance;
			this.availableBalance = availableBalance;
			this.frozenBalance = frozenBalance;
		}

		/**
		 * 总余额：包含可用余额，已冻余额等
		 */
		private BigDecimal balance;

		/**
		 * 可用余额
		 */
		private BigDecimal availableBalance;

		/**
		 * 已冻余额
		 */
		private BigDecimal frozenBalance;

	}

}
