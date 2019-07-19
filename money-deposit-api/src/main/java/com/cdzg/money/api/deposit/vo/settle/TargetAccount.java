package com.cdzg.money.api.deposit.vo.settle;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TargetAccount implements java.io.Serializable {

	private static final long serialVersionUID = 174713979766587792L;

	private String accountId;

	/**
	 * 用户在账户明细可看到的摘要，一般用于描述该笔明细目的/来源等，譬如"收费"
	 */
	private String summary;

	public static TargetAccount instance(String accountId, String summary) {
		TargetAccount ta = new TargetAccount();
		ta.accountId = accountId;
		ta.summary = summary;
		return ta;
	}

}
