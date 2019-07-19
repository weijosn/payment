package com.cdzg.money.api.service.settle;

import com.cdzg.money.api.deposit.vo.DepositBaseResponse;
import com.cdzg.money.api.deposit.vo.settle.FundInRequest;
import com.cdzg.money.api.deposit.vo.settle.SettlementRequest;

/**
 * 账户服务对外结算接口
 * 
 * @author jiangwei
 *
 */
public interface ISettlementService {

	DepositBaseResponse settle(SettlementRequest request);

	/**
	 * 无条件入款
	 */
	DepositBaseResponse fundIn(FundInRequest request);

}
