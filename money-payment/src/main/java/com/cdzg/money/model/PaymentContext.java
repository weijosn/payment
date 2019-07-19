package com.cdzg.money.model;

import java.math.BigDecimal;

import com.cdzg.money.model.enums.PaymentCodeEnum;

import lombok.Getter;
import lombok.Setter;

/**
 * 支付流程上下文内容
 *
 * @author jiangwei
 */
@Getter
@Setter
public class PaymentContext {

	// 支付指令
	private PaymentInstruction paymentInstruction;

	// 商品名称
	private String goodsName;

	// 商品价格
	private BigDecimal goodsPrice;

	// 来源的APPID
	private String appId;

	// 交易备注信息
	private String remark;

	// 商家交易订单号
	private String tradeNo;

	// 支付订单号
	private String paymentOrderId;

	// 原始订单号
	private String orgPaymentOrderId;

	// 支付编码（不同支付编码执行不同的协议）
	private PaymentCodeEnum paymentCode;

}
