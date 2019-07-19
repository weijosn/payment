package com.cdzg.money.api.deposit.vo.account;

import com.cdzg.money.api.deposit.vo.DepositBaseRequest;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AcctsByMemberQueryRequest extends DepositBaseRequest {

	private static final long serialVersionUID = 7786805206711310496L;

	private String memberId;

	/**
	 * 账户子类型列表。譬如[101, 115] <code>null</code>或空数组代表查询该会员下的所有账户
	 */
	private int[] subAccountTypes;

}
