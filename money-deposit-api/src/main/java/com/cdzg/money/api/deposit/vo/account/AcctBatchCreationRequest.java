package com.cdzg.money.api.deposit.vo.account;

import java.util.List;

import com.cdzg.money.api.deposit.vo.DepositBaseRequest;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AcctBatchCreationRequest extends DepositBaseRequest {

	private static final long serialVersionUID = -3105843879939565144L;

	/**
	 * 科目编码
	 */
	private String accountTitleCode;

	/**
	 * 标准币种，譬如"CNY", "USD"等
	 */
	private String currencyCode = "CNY";

	private List<AccountInfo> accounts;

	/**
	 * 目标机构
	 */
	private String targetInstCode;
	
	private AccountTypeEnum accountType;

	@Getter
	@Setter
	public static class AccountInfo implements java.io.Serializable {

		private static final long serialVersionUID = -676029098626757602L;

		/**
		 * 会员编号
		 */
		private String memberId;

		/**
		 * 户名
		 */
		private String accountName;

		/**
		 * 备注
		 */
		private String memo;
	}

}
