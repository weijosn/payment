package com.cdzg.money.api.deposit.vo.account;

import java.util.ArrayList;
import java.util.List;

import com.cdzg.money.api.deposit.vo.DepositBaseResponse;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AcctsQueryResponse extends DepositBaseResponse {
    
	private static final long serialVersionUID = 3950600445041773554L;
	
	private List<AccountVO> accounts = new ArrayList<AccountVO>();

    

}
