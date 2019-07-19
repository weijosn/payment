package com.cdzg.money.api.deposit.vo.account;

import com.cdzg.money.api.deposit.vo.DepositBaseResponse;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AcctCreationResponse extends DepositBaseResponse {

	private static final long serialVersionUID = -45570551141355532L;

	String accountNo;
	
	

}
