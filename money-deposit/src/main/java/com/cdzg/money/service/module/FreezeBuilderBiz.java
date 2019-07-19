package com.cdzg.money.service.module;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.cdzg.money.api.deposit.vo.settle.FreezeAccounting;
import com.cdzg.money.api.deposit.vo.settle.UnfreezeAccounting;
import com.cdzg.money.model.Account;
import com.cdzg.money.model.FreezeInstruction;
import com.cdzg.money.model.UnfreezeInstruction;
import com.cdzg.money.repository.FreezeRepository;

@Component
public class FreezeBuilderBiz {
	
	@Resource
	private FreezeRepository freezeRepository;

	public List<FreezeInstruction> buildFreezeInstructions(List<FreezeAccounting> freezeAccountings, Map<String, Account> beingUsedAccounts) {
		List<FreezeInstruction> freezeInstructions = new ArrayList<FreezeInstruction>();
		if(freezeAccountings != null && !freezeAccountings.isEmpty()) {
			for(FreezeAccounting freezeAccounting : freezeAccountings) {
				String accountId = freezeAccounting.getTargetAccount().getAccountId();
				Account depositAccount = beingUsedAccounts.get(accountId);
				if(depositAccount == null) {
					throw new IllegalArgumentException("the accountId [" + accountId + "] does not exist" );
				}
				FreezeInstruction freezeInstruction = new FreezeInstruction();
				freezeInstruction.setDepositAccount(depositAccount);
				freezeInstruction.setOrigBizNo(freezeAccounting.getOrigBizNo());
				freezeInstruction.setAmount(freezeAccounting.getAmount());
				freezeInstruction.setReason(freezeAccounting.getTargetAccount().getSummary());
				freezeInstruction.setAvailabeUnfrozenAmount(freezeAccounting.getAmount());
				freezeInstructions.add(freezeInstruction);
			}
		}
		return freezeInstructions;		
	}

	public List<UnfreezeInstruction> buildUnfreezeInstructions(List<UnfreezeAccounting> unfreezeAccountings, Map<String, Account> beingUsedAccounts) {
		List<UnfreezeInstruction> unfreezeInstructions = new ArrayList<UnfreezeInstruction>();
		if(unfreezeAccountings != null && !unfreezeAccountings.isEmpty()) {
			for(UnfreezeAccounting unfreezeAccounting : unfreezeAccountings) {
				String accountId = unfreezeAccounting.getTargetAccount().getAccountId();
				Account depositAccount = beingUsedAccounts.get(accountId);
				if(depositAccount == null) {
					throw new IllegalArgumentException("the accountId [" + accountId + "] does not exist" );
				}
				UnfreezeInstruction unfreezeInstruction = new UnfreezeInstruction();
				unfreezeInstruction.setDepositAccount(depositAccount);
				unfreezeInstruction.setOrigBizNo(unfreezeAccounting.getOrigBizNo());
				unfreezeInstruction.setAmount(unfreezeAccounting.getAmount());
				unfreezeInstruction.setReason(unfreezeAccounting.getTargetAccount().getSummary());
				//原冻结请求
				FreezeInstruction origFreezeInstruction = freezeRepository.queryOrigAcctFreeze(unfreezeAccounting.getOrigBizNo(), unfreezeAccounting.getTargetAccount().getAccountId());
				if(origFreezeInstruction == null) {
					throw new IllegalArgumentException("UnfreezeAccounting's origFreezeAccountingId[" + unfreezeAccounting.getOrigBizNo() +"] does not exists"); 
				}
				unfreezeInstruction.setOrigFreezeInstruction(origFreezeInstruction);
				unfreezeInstructions.add(unfreezeInstruction);
			}
		}
		return unfreezeInstructions;
	}

}
