package com.cdzg.money.api.payment;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class PaymentBaseRequest implements java.io.Serializable {

	private static final long serialVersionUID = -2331356163493588916L;

	/**
	 * 来源id
	 */
	private String appId;

	/**
	 * 业务号（唯一）
	 */
	private String tradeNo;

}
