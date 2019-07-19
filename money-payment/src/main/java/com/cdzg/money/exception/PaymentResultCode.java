package com.cdzg.money.exception;

/**
 * 储值系统错误码信息
 * 
 * @author jiangwei
 *
 */
public enum PaymentResultCode implements java.io.Serializable {

	SUCCESS("1000", "处理成功"), // 明确交易成功

	SUSPEND("1002", "协议挂起"), // 异步通知，等待状态
	
	FAIL("9001", "请求处理失败"),

	// 请求参数非法
	ILLEGAL_ARGUMENT("2001", "请求参数非法"),

	NO_PROTOCOL_FOUND("2002", "不存在指定的协议"),

	CHANNEL_SETTLEMENT_ERROR("2003", "外部渠道结算失败"),

	ACCOUNTING_SETTLEMENT_ERROR("2004", "内部渠道结算失败"),

	ACCOUNTING_STAGE_ERROR("2005", "支付阶段更新失败"),

	DUPLICATE_BIZNO("2007", "错误的业务订单号"),
	
	PAYMENT_STATUS_ERROR("2008", "错误的业务状态"),
	
	CLOSE_PAYMENT_ERROR("2010", "关闭原始订单失败"),
	
	PAYMENT_STAGE_STATUS_ERROR("2009", "错误的业务状态（支付阶段）")
	
	;


	private String code;

	private String message;

	private PaymentResultCode(String code, String message) {
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
	public static PaymentResultCode getByCode(String code) {
		if (code == null || code.trim().length() == 0) {
			return null;
		}

		for (PaymentResultCode errorCode : PaymentResultCode.values()) {
			if (errorCode.getCode().equals(code)) {
				return errorCode;
			}
		}
		return null;
	}
}
