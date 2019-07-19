package com.cdzg.money.api.deposit.vo.account;

import com.cdzg.money.api.deposit.vo.DepositBaseResponse;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AcctQueryResponse extends DepositBaseResponse {

	private static final long serialVersionUID = 6000069537369968318L;
	
	private AccountVO account;

}
