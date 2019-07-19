package com.cdzg.money.api.refund.request;

import java.math.BigDecimal;

import com.cdzg.money.api.payment.PaymentBaseResponse;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RefundResult extends PaymentBaseResponse {

	private static final long serialVersionUID = 5233055279698929062L;

	private BigDecimal refundAmount;

	private BigDecimal orderAmount;

	private String orderId;

	private String orderStatus;

}
