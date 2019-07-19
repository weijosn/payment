package com.cdzg.money.mapper.entity;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefundOrderPO {

	private long id;

	// 订单号
	private String paymentOrderId;

	// 退款订单号
	private String refundOrderId;

	// 申请退款金额
	private BigDecimal refundAmount;

	// 实际退款金额
	private BigDecimal actualRefundAmount;

	// 退款状态
	private String refundStatus;

	// 订单创建时间
	private Date createTime;

	// 实际退款到账时间
	private Date refundTime;

	// 开始退款时间
	private Date realyTime;

	// 交易号
	private String tradeNo;
	// 请求来源
	private String appId;

}
