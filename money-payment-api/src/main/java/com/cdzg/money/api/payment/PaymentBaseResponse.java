package com.cdzg.money.api.payment;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentBaseResponse implements java.io.Serializable{

	private static final long serialVersionUID = -6833994583119664967L;

	/**
	 * <code>null</code>代表成功
	 */
	private String resultCode;

	private String resultMessage;

	public boolean isSuccess() {
		return resultCode == null || resultCode.trim().length() == 0;
	}

}
