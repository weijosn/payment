package com.cdzg.money.model.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * 所有支付类型
 * 
 * @author jiangwei
 *
 */
public enum OrderTypeEnum {

	NORMAL("NRL", "一般");

	private final String code;
	private final String desc;

	OrderTypeEnum(String code, String desc) {
		this.code = code;
		this.desc = desc;

	}

	public String getCode() {
		return code;
	}

	public String getDesc() {
		return desc;
	}

	public static OrderTypeEnum getByCode(String code) {
		for (OrderTypeEnum type : OrderTypeEnum.values()) {
			if (StringUtils.equals(code, type.getCode())) {
				return type;
			}
		}
		return null;
	}
}
