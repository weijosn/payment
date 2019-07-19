package com.cdzg.money.api.deposit.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class DepositBaseRequest implements java.io.Serializable {

	private static final long serialVersionUID = -2331356163493588916L;

	private String appId; // 请求来源

	private String bizNo; // 请求业务码

}
