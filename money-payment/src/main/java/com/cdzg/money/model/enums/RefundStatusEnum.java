package com.cdzg.money.model.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * 订单状态
 * 
 * @author jiangwei
 *
 */
public enum RefundStatusEnum {

	INIT("I", "待退款"),

	PENDING("P", "退款进行中"),

	SUCCESS("PS", "退款成功"),

	FAILED("F", "退款失败");

	private final String code;
	private final String desc;

	RefundStatusEnum(String code, String desc) {
		this.code = code;
		this.desc = desc;

	}

	public String getCode() {
		return code;
	}

	public String getDesc() {
		return desc;
	}

	public static RefundStatusEnum getByCode(String code) {
		for (RefundStatusEnum type : RefundStatusEnum.values()) {
			if (StringUtils.equals(code, type.getCode())) {
				return type;
			}
		}
		return null;
	}
}
