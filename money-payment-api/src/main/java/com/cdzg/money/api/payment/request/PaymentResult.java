package com.cdzg.money.api.payment.request;

import java.math.BigDecimal;

import com.cdzg.money.api.payment.PaymentBaseResponse;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PaymentResult extends PaymentBaseResponse {

	private static final long serialVersionUID = 5233055279698929062L;

	// 实付金额
	private BigDecimal paiedAmount = BigDecimal.ZERO;

	//订单金额
	private BigDecimal orderAmount = BigDecimal.ZERO;

	// 订单号
	private String orderId;

	// 订单状态
	private String orderStatus;

	// 扩展参数
	private Extension extension;

}
