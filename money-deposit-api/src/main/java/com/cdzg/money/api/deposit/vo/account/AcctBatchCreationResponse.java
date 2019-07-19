package com.cdzg.money.api.deposit.vo.account;

import java.util.ArrayList;
import java.util.List;

import com.cdzg.money.api.deposit.vo.DepositBaseResponse;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AcctBatchCreationResponse extends DepositBaseResponse {

	private static final long serialVersionUID = -45570551141355532L;

	List<AccountMap> accounts = new ArrayList<AccountMap>();

	@Getter
	@Setter
	public static class AccountMap implements java.io.Serializable {

		private static final long serialVersionUID = -3299474284541146903L;

		String memberId;

		String accountNo;
		
		
	}

}
