package com.cdzg.money.exception;


public class PaymentException extends RuntimeException {
	
	private static final long serialVersionUID = 1857440708804128584L;
	private PaymentResultCode errorCode;

	public PaymentException(PaymentResultCode errorCode, String msg) {

		this(errorCode, msg, null);
	}

	public PaymentException(String msg) {
		this(PaymentResultCode.FAIL, msg);
	}

	public PaymentException(String msg, Throwable cause) {
		this(PaymentResultCode.FAIL, msg, cause);
	}

	public PaymentException(PaymentResultCode errorCode, String msg, Throwable cause) {
		super(msg, cause);
		if (errorCode == null) {
			throw new IllegalArgumentException("errorCode is null");
		}
		this.errorCode = errorCode;
	}

	public PaymentResultCode getErrorCode() {
		return errorCode;
	}
}
