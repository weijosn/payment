package com.cdzg.money.service.impl.account;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.cdzg.money.api.channel.common.PartyInfo;
import com.cdzg.money.api.channel.request.OpenAccountRequest;
import com.cdzg.money.api.channel.request.OpenAccountResponse;
import com.cdzg.money.api.channel.settle.IChannelService;
import com.cdzg.money.api.deposit.vo.DepositBaseResponse;
import com.cdzg.money.api.deposit.vo.account.AcctBatchCreationRequest;
import com.cdzg.money.api.deposit.vo.account.AcctBatchCreationResponse;
import com.cdzg.money.api.deposit.vo.account.AcctCreationRequest;
import com.cdzg.money.api.deposit.vo.account.AcctCreationResponse;
import com.cdzg.money.api.deposit.vo.account.AcctQueryResponse;
import com.cdzg.money.api.deposit.vo.account.AcctStatusChangeRequest;
import com.cdzg.money.api.deposit.vo.account.AcctsByMemberQueryRequest;
import com.cdzg.money.api.deposit.vo.account.AcctsQueryResponse;
import com.cdzg.money.api.service.account.IAccountService;
import com.cdzg.money.exception.DepositResultCode;
import com.cdzg.money.service.module.AccountBiz;

import lombok.extern.slf4j.Slf4j;

/**
 * 账户对外接口
 * 
 * @author jiangwei
 *
 */
@Component
@Slf4j
@Service(version = "1.0.0")
public class AccountServiceImpl implements IAccountService {

	@Autowired
	private AccountBiz accountBiz;
	@Reference(version = "1.0.0", check = false, timeout = 999999999, retries = -1)
	private IChannelService channelService;

	@Override
	public AcctsQueryResponse queryAcctsByMember(AcctsByMemberQueryRequest request) {
		return accountBiz.queryAcctsByMember(request);
	}

	@Override
	public AcctCreationResponse createAcct(AcctCreationRequest request) {
		return newAcct(request);
	}

	@Override
	public AcctBatchCreationResponse createAcct(AcctBatchCreationRequest request) {
		AcctBatchCreationResponse rep = new AcctBatchCreationResponse();
		try {
			// 创建内部户
			Map<String, String> result = accountBiz.createAcct(request);
			
			for (Map.Entry<String, String> acc : result.entrySet()) {
				AcctBatchCreationResponse.AccountMap am = new AcctBatchCreationResponse.AccountMap();
				am.setAccountNo(acc.getValue());
				am.setMemberId(acc.getKey());

				// 开户成功-调用渠道开设外部户
				if (StringUtils.isNotEmpty(request.getTargetInstCode())) {
					OpenAccountRequest openAccount = new OpenAccountRequest();
					openAccount.setAppId("deposit");
					openAccount.setAccountType(request.getAccountType().getCode());
					openAccount.setBizNo(request.getBizNo());
					openAccount.setPartyInfo(PartyInfo.instance(am.getAccountNo(), am.getMemberId()));
					openAccount.setTargetInstCode(request.getTargetInstCode());
					if ( channelService.openAccount(openAccount).isSuccess() ) {
						rep.getAccounts().add(am);
					}
				} else {
					rep.getAccounts().add(am);
				}
			}
			rep.setResultCode(DepositResultCode.SUCCESS.getCode());
			rep.setResultMessage(DepositResultCode.SUCCESS.getCode());
		} catch (java.lang.IllegalArgumentException e) {
			rep.setResultCode(DepositResultCode.ILLEGAL_ARGUMENT.getCode());
			rep.setResultMessage(e.getMessage());
		} catch (Exception e) {
			log.error("create internal accout error.", e);
			rep.setResultCode(DepositResultCode.FAIL.getCode());
			rep.setResultMessage(e.getMessage());
		}
		return rep;
	}

	private AcctCreationResponse newAcct(AcctCreationRequest request) {
		
		AcctCreationResponse rep = new AcctCreationResponse();
		
		try {
			
			// 创建账户
			rep.setAccountNo(accountBiz.createAcct(request));
			
			// 开户成功-调用渠道开设外部户
			if (StringUtils.isNotEmpty(rep.getAccountNo()) && StringUtils.isNotEmpty(request.getTargetInstCode())) {
				OpenAccountRequest openAccount = new OpenAccountRequest();
				openAccount.setAppId(request.getAppId());
				openAccount.setAccountType(request.getAccountType().getCode());//开户类型
				openAccount.setBizNo(request.getBizNo());
				openAccount.setPartyInfo(PartyInfo.instance(rep.getAccountNo(), request.getMemberId()));
				openAccount.setTargetInstCode(request.getTargetInstCode());
				OpenAccountResponse channelRep = channelService.openAccount(openAccount);
				if (channelRep.isSuccess()) {
		            rep.setResultCode(DepositResultCode.SUCCESS.getCode());
		            rep.setResultMessage(DepositResultCode.SUCCESS.getMessage());
				} else {
					log.warn("channel open account error , accountNo {} , error message {}", rep.getAccountNo(),channelRep.toString());
					rep.setAccountNo(null);
					rep.setResultCode(DepositResultCode.CHANNEL_ERROR.getCode());
					rep.setResultMessage(channelRep.getResultMessage());
				}
			}
		} catch (IllegalArgumentException e) {
			rep.setResultCode(DepositResultCode.ILLEGAL_ARGUMENT.getCode());
			rep.setResultMessage(e.getMessage());
			log.error("create internal account error.{}", e.getMessage());
		} catch (Exception e) {
			log.error("create internal account error.", e);
			rep.setResultCode(DepositResultCode.FAIL.getCode());
			rep.setResultMessage(e.getMessage());
		}
		return rep;
	}

	@Override
	public DepositBaseResponse changeAcctStatus(AcctStatusChangeRequest request) {
		return this.accountBiz.changeAcctStatus(request);
	}

	@Override
	public AcctQueryResponse findByAccountId(String accountId) {
		AcctQueryResponse rep = new AcctQueryResponse();
		try {
			rep.setAccount(accountBiz.findByAccountId(accountId));
            rep.setResultCode(DepositResultCode.SUCCESS.getCode());
            rep.setResultMessage(DepositResultCode.SUCCESS.getMessage());
		} catch (java.lang.IllegalArgumentException e) {
			rep.setResultCode(DepositResultCode.ILLEGAL_ARGUMENT.getCode());
			rep.setResultMessage(e.getMessage());
		} catch (Exception e) {
			log.error("query internal accout error.", e);
			rep.setResultCode(DepositResultCode.FAIL.getCode());
			rep.setResultMessage(e.getMessage());
		}
		return rep;
	}

}
