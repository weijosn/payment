package com.cdzg.money.api.service.account;

import com.cdzg.money.api.deposit.vo.DepositBaseResponse;
import com.cdzg.money.api.deposit.vo.account.AcctBatchCreationRequest;
import com.cdzg.money.api.deposit.vo.account.AcctBatchCreationResponse;
import com.cdzg.money.api.deposit.vo.account.AcctCreationRequest;
import com.cdzg.money.api.deposit.vo.account.AcctCreationResponse;
import com.cdzg.money.api.deposit.vo.account.AcctQueryResponse;
import com.cdzg.money.api.deposit.vo.account.AcctStatusChangeRequest;
import com.cdzg.money.api.deposit.vo.account.AcctsByMemberQueryRequest;
import com.cdzg.money.api.deposit.vo.account.AcctsQueryResponse;

/**
 * 储值对外查询接口
 * 
 * @author jiangwei
 *
 */
public interface IAccountService {

	/**
	 * 根据会员ID查询账户
	 * 
	 * @param request
	 * @return
	 */
	AcctsQueryResponse queryAcctsByMember(AcctsByMemberQueryRequest request);

	/**
	 * 创建内部户
	 *
	 * @param request
	 * @return
	 */
	public AcctCreationResponse createAcct(AcctCreationRequest request);

	/**
	 * 批量开户接口
	 * 
	 * @param request
	 * @return
	 */
	public AcctBatchCreationResponse createAcct(AcctBatchCreationRequest request);

	/**
	 * 改变账户状态
	 *
	 * @param request
	 */
	public DepositBaseResponse changeAcctStatus(AcctStatusChangeRequest request);

	/**
	 * 根据账号ID查询
	 * 
	 * @param payeeAccountId
	 * @return
	 */
	AcctQueryResponse findByAccountId(String accountId);

}
