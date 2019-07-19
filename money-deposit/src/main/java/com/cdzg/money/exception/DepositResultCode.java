package com.cdzg.money.exception;

/**
 * 储值系统错误码信息
 * 
 * @author jiangwei
 *
 */
public enum DepositResultCode {

	SUCCESS("1000","处理成功"),
	
	// 请求参数非法
	ILLEGAL_ARGUMENT("2001", "请求参数非法"),

	ACCOUNT_STATUS_MISMATCH("2002", "当前账户状态不允许该操作"),

	TRANSACTION_STATUS_ERROR("2003", "当前账务状态不允许该操作"),

	ACCOUNT_NOT_FOUND("2004", "指定的账户不存在"),

	CHANNEL_ERROR("2005", "渠道开户失败"),

	ACCOUNT_BALANCE_NOT_ENOUGH("2006", "账户余额不足"),

	// 请求按流程做完，明确失败,客户端无需关注失败原因
	FAIL("9001", "请求处理失败"),

	// 请求处理过程中，出现未知错误
	UNKNOWN("9002", "未知错误，系统异常"),

	;

	private String code;

	private String message;

	private DepositResultCode(String code, String message) {
		this.code = code;
		this.message = message;
	}

	public String getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

	/**
	 * 通过代码获取枚举项
	 * 
	 * @param code
	 * @return
	 */
	public static DepositResultCode getByCode(String code) {
		if (code == null || code.trim().length() == 0) {
			return null;
		}

		for (DepositResultCode errorCode : DepositResultCode.values()) {
			if (errorCode.getCode().equals(code)) {
				return errorCode;
			}
		}
		return null;
	}
}
