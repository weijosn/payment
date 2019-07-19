
package com.cdzg.money.api.payment.request;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

/**
 * 支付订单信息[兼容以前的数据]
 *
 * @author jiangwei
 */
@Getter
@Setter
public class PaymentOrderInfo implements java.io.Serializable {
	/**
	 * 序列化ID
	 */
	private static final long serialVersionUID = -5809782578272943999L;

	/**
	 * 支付总金额
	 */
	private BigDecimal payAmount;

	/**
	 * 异步通知地址
	 */
	private String notifyurl;

	/**
	 * 支付备注
	 */
	private String payRemark;

	/**
	 * 商品名称
	 */
	private String goodsName;

	/**
	 * 支付原始订单号（退款必须）
	 */
	private String paymentOrderId;

	/**
	 * 原始支付订单号
	 */
	private String orgPaymentOrderId;

}
