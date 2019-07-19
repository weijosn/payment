package com.cdzg.money.mapper.entity;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClusterLockPO {

	private int id;
	private String lockName;
	private String taskName;
	private boolean lockStatus;
	private Date lockTime;
	private int overSecond;
}