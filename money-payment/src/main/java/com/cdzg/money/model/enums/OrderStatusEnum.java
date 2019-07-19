package com.cdzg.money.model.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * 订单状态
 * 
 * @author jiangwei
 *
 */
public enum OrderStatusEnum {

	// I 待处理 P 进行中 PS 成功 F 失败 U 未知（待查询）

	INIT("I", "待支付"),

	PENDING("P", "付款中"),

	SUCCESS("PS", "支付成功"),

	REFUND("R", "已退款"),

	FAILED("F", "支付失败");

	private final String code;
	private final String desc;

	OrderStatusEnum(String code, String desc) {
		this.code = code;
		this.desc = desc;

	}

	public String getCode() {
		return code;
	}

	public String getDesc() {
		return desc;
	}

	public static OrderStatusEnum getByCode(String code) {
		for (OrderStatusEnum type : OrderStatusEnum.values()) {
			if (StringUtils.equals(code, type.getCode())) {
				return type;
			}
		}
		return null;
	}

	public static boolean refundable(String code) {
		OrderStatusEnum status = OrderStatusEnum.getByCode(code);
		return status == SUCCESS;
	}
}
