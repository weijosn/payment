package com.cdzg.money.api.payment.request;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * <p>
 * 参与方信息
 * </p>
 */
@Getter
@Setter
public class PartyInfo implements java.io.Serializable {
	
	private static final long serialVersionUID = -9192844446680931328L;

	/** 储值账户: 非空 */
	private String accountNo;

	private Extension extension;

	private String memberId;

	public static PartyInfo instance(String accountNo, String memberId) {
		PartyInfo pi = new PartyInfo();
		pi.accountNo = accountNo;
		pi.memberId = memberId;
		return pi;
	}

}
