package com.cdzg.money.mapper.entity;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentRequestPO implements java.io.Serializable {

	private static final long serialVersionUID = 741231858441822688L;

	private String paymentCode;

	private String paymentOrderId;

	private String paymentVersion;

	private Date createTime;

	private String bizNo;
	
	private String remark;
	
	private String appId;

}
