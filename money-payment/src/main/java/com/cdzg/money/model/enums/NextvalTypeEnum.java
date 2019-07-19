package com.cdzg.money.model.enums;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.apache.commons.lang3.StringUtils;

/**
 * 订单状态
 * 
 * @author jiangwei
 *
 */
public enum NextvalTypeEnum {

	// P 进行中 PS 成功 F 失败 U 未知（待查询）

	ORDER("ORDER", "支付订单", "1"), REFUND("REFUND", "退款订单", "2");

	private final String desc, prefix, code;
	private final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyyMMdd");

	NextvalTypeEnum(String code, String desc, String prefix) {
		this.code = code;
		this.desc = desc;
		this.prefix = prefix;
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

	public String wrap(String nextval) {
		StringBuilder buf = new StringBuilder();
		buf.append(LocalDate.now().format(FMT));
		buf.append(prefix);
		buf.append(StringUtils.leftPad(nextval, 10, "0"));
		return buf.append(nextval).toString();
	}
}
