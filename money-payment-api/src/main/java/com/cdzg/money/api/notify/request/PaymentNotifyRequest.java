package com.cdzg.money.api.notify.request;

import java.math.BigDecimal;

import com.cdzg.money.api.payment.PaymentBaseRequest;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(exclude = { "paiedAmount", "channelOrderNo", "paymentOrderNo" })
public class PaymentNotifyRequest extends PaymentBaseRequest {

	private static final long serialVersionUID = 1L;

	// 渠道实际支付金额
	private BigDecimal paiedAmount;

	// 渠道订单号
	private String channelOrderNo;

	// 支付订单号(支付服务的)
	private String paymentOrderNo;

}
