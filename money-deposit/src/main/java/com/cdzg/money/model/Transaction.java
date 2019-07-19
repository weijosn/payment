package com.cdzg.money.model;

import com.cdzg.money.model.enums.TransactionStatusEnum;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Transaction {

	/**
	 * 业务流水号
	 */
	private String bizno;

	/**
	 * 交易状态，默认都是成功的交易状态 1：正常状态 2:作废状态 3:被红
	 */
	private TransactionStatusEnum status;

	/**
	 * VARCHAR(32)<br>
	 * 调用方应用编号
	 */
	private String appid;

}
