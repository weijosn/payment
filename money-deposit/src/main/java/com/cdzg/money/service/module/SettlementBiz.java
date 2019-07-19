package com.cdzg.money.service.module;

import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.Assert;

import com.cdzg.money.api.deposit.vo.settle.FundInRequest;
import com.cdzg.money.api.deposit.vo.settle.SettlementRequest;
import com.cdzg.money.exception.DepositException;
import com.cdzg.money.exception.DepositResultCode;
import com.cdzg.money.exception.ExceptionUtils;
import com.cdzg.money.model.Account;
import com.cdzg.money.model.AccountingEntry;
import com.cdzg.money.model.FreezeInstruction;
import com.cdzg.money.model.SettlementInstruction;
import com.cdzg.money.model.Transaction;
import com.cdzg.money.model.UnfreezeInstruction;
import com.cdzg.money.model.enums.DrCr;
import com.cdzg.money.model.enums.TransactionStatusEnum;
import com.cdzg.money.repository.AccountRepository;
import com.cdzg.money.repository.FreezeRepository;
import com.cdzg.money.repository.TransactionRepository;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SettlementBiz implements ApplicationContextAware {

	@Autowired
	private TransactionTemplate depositTransactionTemplate;
	@Autowired
	private SettlementValidatorBiz settlementRequestValidatorBiz;
	@Autowired
	private AccountingBiz accountingBiz;
	@Autowired
	private AccountRepository accountRepository;
	@Autowired
	private FreezeRepository freezeRepository;
	@Autowired
	private TransactionRepository transactionRepository;

	private ApplicationContext applicationContext;

	public boolean findIn(FundInRequest request) {

		final AccountingEntry fundInInstruction = buildFundInstruction(request);
		final Transaction transaction = this.transactionRepository.getTransaction(request.getBizNo(), request.getAppId());
		
		if (transaction != null && transaction.getStatus() == TransactionStatusEnum.Valid) {
			return true;
		}
		// 开始账务处理
		try {
			
			// 记录事务（相同的数据无法提交上来）
			transfer(request.getBizNo(), request.getAppId());

			depositTransactionTemplate.execute(new TransactionCallbackWithoutResult() {
				@Override
				protected void doInTransactionWithoutResult(TransactionStatus status) {
					// 入账
					accountingBiz.doPairSuites(fundInInstruction,request.getSuiteNo());
					// 完成事务
					commit(request.getBizNo(),request.getAppId());
				}
			});

			return true;
		}
		//这里只操作唯一索引的问题
		catch(Exception e) {
			throw e;
		} finally {
			transactionRepository.invalid(request.getBizNo(),request.getAppId());
		}
		
	}

	public boolean settle(final SettlementRequest settlementRequest) {

		// 校验请求参数
		settlementRequestValidatorBiz.validate(settlementRequest);
		
		// 构建并校验结算指令
		final SettlementInstruction settlementInstruction = buildAndValidateSettlement(settlementRequest);

		final Transaction transaction = this.transactionRepository.getTransaction(settlementRequest.getBizNo(), settlementRequest.getAppId());

		if (transaction != null && transaction.getStatus() == TransactionStatusEnum.Valid) {
			return true;
		}

		// 开始账务处理
		try {
			
			// 记录事务（相同的数据无法提交上来）
			transfer(settlementRequest.getBizNo(), settlementRequest.getAppId());

			depositTransactionTemplate.execute(new TransactionCallbackWithoutResult() {
	
				@Override
				protected void doInTransactionWithoutResult(TransactionStatus status) {
					
					// 先解冻
					unfreeze(settlementInstruction.getUnfreezeInstructions());
	
					// 再入账
					accountingBiz.doPairSuites(settlementInstruction.getSuites());
	
					// 最后冻结
					freeze(settlementInstruction.getFreezeInstructions());
					
					// 完成事务
					commit(settlementRequest.getBizNo(),settlementRequest.getAppId());
	
				}
			});

			return true;
		}
		//这里只操作唯一索引的问题
		catch(DataAccessException e) {
			// 如果是违反唯一性约束，则表明已存在账务
			if (ExceptionUtils.isDuplicate(e)) {
				log.warn("settlement Duplicate error,requestNo {}", settlementRequest.getBizNo());
				throw new DepositException(DepositResultCode.TRANSACTION_STATUS_ERROR, DepositResultCode.TRANSACTION_STATUS_ERROR.getMessage());
			} else {
				throw e;
			}
		} finally {
			transactionRepository.invalid(settlementRequest.getBizNo(),settlementRequest.getAppId());
		}

	}

	private SettlementInstruction buildAndValidateSettlement(SettlementRequest settlementRequest) {
		SettlementInstruction settlementInstruction = applicationContext.getBean(SettlementInstruction.class);
		settlementInstruction.buildAndValidate(settlementRequest);
		return settlementInstruction;
	}

	private AccountingEntry buildFundInstruction(FundInRequest request) {
		AccountingEntry entry =  new AccountingEntry();
		Account account = accountRepository.findByAccountId(request.getAccount().getAccountId());
		Assert.isTrue(account!=null,"accountNo error");
		entry.setAmount(request.getAmount());
		entry.setDepositAccount(account);
		entry.setDrCr(DrCr.Credit);
		entry.setMemo(request.getAccount().getSummary());
		entry.setNeedsBufferAccouting(false);
		return entry;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	private void unfreeze(List<UnfreezeInstruction> unfreezeInstructions) {
		for (UnfreezeInstruction unfreezeInstruction : unfreezeInstructions) {
			freezeRepository.unfreeze(unfreezeInstruction);
		}
	}
	
	private void transfer(String requestNo,String appId) {
		final Transaction transaction = transactionRepository.getTransaction(requestNo, appId);
		if (transaction == null) {
			transactionRepository.save(requestNo, appId);
			return;
		}
		if (transaction.getStatus() != TransactionStatusEnum.Invalid) {
			throw new DepositException(DepositResultCode.TRANSACTION_STATUS_ERROR, DepositResultCode.TRANSACTION_STATUS_ERROR.getMessage());
		}
	}

	private void commit(String requestNo, String appId) {
		if (transactionRepository.commit(requestNo, appId) != 1) {
			throw new DepositException(DepositResultCode.TRANSACTION_STATUS_ERROR,DepositResultCode.TRANSACTION_STATUS_ERROR.getMessage());
		}
	}

	private void freeze(List<FreezeInstruction> freezeInstructions) {
		for (FreezeInstruction freezeInstruction : freezeInstructions) {
			freezeRepository.freeze(freezeInstruction);
		}
	}

}
