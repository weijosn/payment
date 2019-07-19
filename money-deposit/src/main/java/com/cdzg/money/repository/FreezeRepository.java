package com.cdzg.money.repository;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cdzg.money.exception.DepositResultCode;
import com.cdzg.money.exception.DepositException;
import com.cdzg.money.mapper.AccountFrozenMapper;
import com.cdzg.money.mapper.entity.AccountFrozenPO;
import com.cdzg.money.model.FreezeInstruction;
import com.cdzg.money.model.UnfreezeInstruction;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class FreezeRepository {

	@Autowired
	private AccountRepository accountRepository;
	@Autowired
	private AccountFrozenMapper accountFrozenMapper;

	public void freeze(FreezeInstruction freezeInstruction) {
		saveFreeze(buildPO(freezeInstruction));
		accountRepository.freezeBalance(freezeInstruction.getDepositAccount().getAccountNo(),freezeInstruction.getAmount());
	}

	public void unfreeze(UnfreezeInstruction unfreezeInstruction) {
		unfreeze(unfreezeInstruction.getOrigBizNo(),unfreezeInstruction.getAmount());
		accountRepository.unfreezeBalance(unfreezeInstruction.getDepositAccount().getAccountNo(),unfreezeInstruction.getAmount());
	}

	private void unfreeze(String origBizNo,BigDecimal amount) {
		if (this.accountFrozenMapper.unfreeze(origBizNo,amount) != 1) {
			log.error("unfreeze error . origBizNo {}", origBizNo);
			throw new DepositException(DepositResultCode.FAIL, "解冻记录操作失败！");
		}
	}

	private void saveFreeze(AccountFrozenPO buildPO) {
		this.accountFrozenMapper.saveFreeze(buildPO);
	}

	public FreezeInstruction queryOrigAcctFreeze(String origBizNo, String accountId) {
		return toFreezeInstruction(accountFrozenMapper.findByOrigBizNo(origBizNo,accountId));
	}

	private FreezeInstruction toFreezeInstruction(AccountFrozenPO po) {
		if(po==null)
			return null;
		FreezeInstruction fi = new FreezeInstruction();
		fi.setAmount(po.getAmount());
		fi.setAvailabeUnfrozenAmount(po.getAvailUnfrozenAmount());
		fi.setOrigBizNo(po.getOrigBizNo());
		fi.setDepositAccount(accountRepository.findByAccountId(po.getAccountId()));
		fi.setReason(po.getReason());
		return fi;
	}

	private AccountFrozenPO buildPO(FreezeInstruction freezeInstruction) {
		AccountFrozenPO po = new AccountFrozenPO();
		po.setAccountId(freezeInstruction.getDepositAccount().getAccountNo());
		po.setAmount(freezeInstruction.getAmount());
		po.setAvailBalance(freezeInstruction.getDepositAccount().getAvailablebalance());
		po.setAvailUnfrozenAmount(freezeInstruction.getAvailabeUnfrozenAmount());
		po.setInsertDate(new Date());
		po.setOrigBizNo(freezeInstruction.getOrigBizNo());
		po.setReason(StringUtils.left(freezeInstruction.getReason(), 30));
		po.setFreezeType(1);
		po.setStatus(1);
		po.setReason(freezeInstruction.getReason());
		return po;
	}

}
