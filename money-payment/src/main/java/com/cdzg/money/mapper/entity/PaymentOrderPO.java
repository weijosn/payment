package com.cdzg.money.mapper.entity;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentOrderPO {

	private long id;

	private String paymentOrderId;

	// 商品名称
	private String goodsName;
	// 商品金额
	private BigDecimal goodsPrice;

	// 应付
	private BigDecimal orderAmount;
	// 实付
	private BigDecimal paiedAmount;

	// 支付代码
	private String paymentCode;
	// 订单状态
	private String orderStatus;

	// 支付时间
	private Date paymentTime;
	// 准备支付时间
	private Date readlyTime;
	// 创建时间
	private Date createTime;
	// 修改时间
	private Date updateTime;

	// 商家编号
	private String appId;
	// 外部订单号
	private String tradeNo;

	// 订单类型
	private String orderType;
	// 备注
	private String remark;

}
