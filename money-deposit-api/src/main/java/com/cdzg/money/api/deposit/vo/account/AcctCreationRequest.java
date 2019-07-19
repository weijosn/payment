package com.cdzg.money.api.deposit.vo.account;

import com.cdzg.money.api.deposit.vo.DepositBaseRequest;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AcctCreationRequest extends DepositBaseRequest {

	private static final long serialVersionUID = -3105843879939565144L;

	/**
	 * 会员编号
	 */
	private String memberId;

	/**
	 * 标准币种，譬如"CNY", "USD"等
	 */
	private String currencyCode = "CNY";

	/**
	 * 科目编码
	 */
	private String accountTitleCode;

	/**
	 * 目标机构
	 */
	private String targetInstCode = "";

	/**
	 * 户名
	 */
	private String accountName = "";

	/**
	 * 备注
	 */
	private String memo = "";

	private AccountTypeEnum accountType = AccountTypeEnum.Individual;

}
