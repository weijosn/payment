package com.cdzg.money.model;

import java.util.Date;

import com.cdzg.money.model.enums.TransactionStatusEnum;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PairAccountSuite {
	
	/**
	 * 套号
	 */
	private String suiteNo;
	/**
	 * 币种
	 */
	private Currency currency;
	/**
	 * 交易状态
	 */
	private TransactionStatusEnum transactionStatus = TransactionStatusEnum.Valid;
	/**
	 * 结算时间
	 */
	private Date settlementTime;
	/**
	 * 借方分录
	 */
	private AccountingEntry debitEntry = new AccountingEntry();

	/**
	 * 贷方分录
	 */
	private AccountingEntry creditEntry = new AccountingEntry();


}
