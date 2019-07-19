package com.cdzg.money.mapper.entity;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotifyPO {

	// 通知状态
	public final static int NOTIFY_OK = 2, NOTIFY_ING = 1, NOTIFY_WAIT = 0, NOTIFY_REJECT = 3;

	private Long id;
	
	private String paymentOrderNo;

	private String notifyUrl;

	private int notifyStatus = NOTIFY_WAIT;

	private String goodsName = StringUtils.EMPTY;

	private int responseCode = 0;
	// 时间
	private Date notifyTime;
	private Date createTime;

	private Integer notifyTimes = 0;
}