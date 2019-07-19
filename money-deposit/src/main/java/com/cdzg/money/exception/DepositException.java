package com.cdzg.money.exception;

public class DepositException extends RuntimeException {
	
	private static final long serialVersionUID = 1857440708804128584L;
	private DepositResultCode errorCode;

	public DepositException(DepositResultCode errorCode, String msg) {

		this(errorCode, msg, null);
	}

	public DepositException(String msg) {
		this(DepositResultCode.FAIL, msg);
	}

	public DepositException(String msg, Throwable cause) {
		this(DepositResultCode.FAIL, msg, cause);
	}

	public DepositException(DepositResultCode errorCode, String msg, Throwable cause) {

		super(msg, cause);
		if (errorCode == null) {
			throw new IllegalArgumentException("errorCode is null");
		}
		this.errorCode = errorCode;
	}

	public DepositResultCode getErrorCode() {
		return errorCode;
	}
}
