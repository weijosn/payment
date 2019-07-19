package com.cdzg.money.api.payment.request;

import com.cdzg.money.api.payment.PaymentBaseRequest;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * <p>
 * 支付请求信息
 * </p>
 * 
 * @author jiangwei
 */
@Getter
@Setter
public class PaymentRequest extends PaymentBaseRequest {

	private static final long serialVersionUID = 7182367875640916616L;


	/**
	 * 参与方信息
	 */
	private PaymentPartyInfo paymentPartyInfo;

	/** 支付订单信息 */
	private PaymentOrderInfo paymentOrderInfo;

	/** 扩展信息 */
	private Extension extension;

}
