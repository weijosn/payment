package com.cdzg.money.model.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * 订单状态
 * 
 * @author jiangwei
 *
 */
public enum NextvalTypeEnum {

	// P 进行中 PS 成功 F 失败 U 未知（待查询）

	ORDER("ORDER", "订单");

	private final String code;
	private final String desc;

	NextvalTypeEnum(String code, String desc) {
		this.code = code;
		this.desc = desc;

	}

	public String getCode() {
		return code;
	}

	public String getDesc() {
		return desc;
	}

	public static NextvalTypeEnum getByCode(String code) {
		for (NextvalTypeEnum type : NextvalTypeEnum.values()) {
			if (StringUtils.equals(code, type.getCode())) {
				return type;
			}
		}
		return null;
	}
}
