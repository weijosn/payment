package com.cdzg.money.mapper.entity;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionPO {

	private long id;
	private String bizNo;
	private Date insertTime;
	private String appId;
	private int status;

}
