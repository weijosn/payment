package com.cdzg.money.mapper.entity;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentStagePO {

	private long id;
	
	
	private String stageOrderId;
	
	private String paymentOrderId;

	private String orderStatus;
	
	private String payType;
	private String payPlat;
	private BigDecimal feeAmount;

	private BigDecimal orderAmount;
	private BigDecimal paiedAmount;

	private Date paymentTime;
	private Date readlyTime;
	private Date createTime;

	private String summary;

	private String payerAccountId;

	private String payeeAccountId;

	public String buildFreeznId() {
		return paymentOrderId + "_" + id;
	}

}
