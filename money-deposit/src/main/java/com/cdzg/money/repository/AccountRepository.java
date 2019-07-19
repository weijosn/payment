package com.cdzg.money.repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cdzg.money.api.deposit.vo.account.AccountStatusEnum;
import com.cdzg.money.api.deposit.vo.account.AccountTypeEnum;
import com.cdzg.money.exception.DepositResultCode;
import com.cdzg.money.exception.DepositException;
import com.cdzg.money.mapper.AccountMapper;
import com.cdzg.money.mapper.entity.AccountPO;
import com.cdzg.money.model.Account;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AccountRepository {

	@Autowired
	private AccountMapper accountMapper;
	@Autowired
	private AccountTitleRepository accountTitleRepository;
	@Autowired
	private CurrencyRepository currencyRepository;

	public void save(Account account) {
		AccountPO po = new AccountPO();
		po.setAccountId(account.getAccountNo());
		po.setAccountName(account.getAccountName());
		po.setAccountTitleId(account.getAcctTitle().getId());
		po.setAccountType(account.getAccountType().getCode());
		po.setAvailableBalance(BigDecimal.ZERO);
		po.setBalance(BigDecimal.ZERO);
		po.setCreateTime(new Date());
		po.setCurrencyType(account.getCurrency().getCurrencyType());
		po.setFrozenBalance(BigDecimal.ZERO);
		po.setLastBalance(BigDecimal.ZERO);
		po.setLastUpdateTime(new Date());
		po.setMemberId(account.getMemberId());
		po.setMemo(account.getMemo());
		po.setStatus(AccountStatusEnum.Active.getCode());
		this.accountMapper.save(po);
	}

	public Account findByAccountId(String acctId) {
		return toAccount(accountMapper.findByAccountId(acctId));
	}

	private Account toAccount(AccountPO ac) {
		Account account = new Account();
		account.setAccountNo(ac.getAccountId());
		account.setAccountName(ac.getAccountName());
		account.setAccountType(AccountTypeEnum.getByCode(ac.getAccountType()));
		account.setAcctTitle(accountTitleRepository.findById(ac.getAccountTitleId()));
		account.setBalance(ac.getBalance());
		account.setCurrency(currencyRepository.findByType(ac.getCurrencyType()));
		account.setFrozenbalance(ac.getFrozenBalance());
		account.setMemberId(ac.getMemberId());
		account.setMemo(ac.getMemo());
		account.setStatus(AccountStatusEnum.getByCode(ac.getStatus()));
		account.setAvailablebalance(ac.getAvailableBalance());
		return account;
	}

	public List<Account> queryAcctsByMemberId(String memberId) {
		List<Account> accts = new ArrayList<Account>();
		this.accountMapper.listByMemberId(memberId).stream().forEach(value -> {
			accts.add(toAccount(value));
		});
		return accts;
	}

	public boolean changeAcctStatus(String accountId, AccountStatusEnum targetStatus) {
		return accountMapper.updateStatusTo(accountId, targetStatus.getCode()) == 1;
	}

	@SuppressWarnings("unchecked")
	public Map<String, Account> listByAccountIds(Set<String> accountIds) {
		if (accountIds.isEmpty()) {
			return Collections.EMPTY_MAP;
		}
		Map<String, Account> maps = new HashMap<String, Account>();
		accountMapper.selectByIdSet(accountIds).stream().forEach(value -> {
			maps.put(value.getAccountId(), toAccount(value));
		});
		return maps;
	}

	public void freezeBalance(String accountId, BigDecimal amount) {
		if (1 != accountMapper.freezeBalance(accountId, amount)) {
			log.error("freeze balance error . accountId {}", accountId);
			throw new DepositException(DepositResultCode.FAIL, "冻结失败！");
		}
	}

	public void unfreezeBalance(String accountId, BigDecimal amount) {
		if (1 != accountMapper.unfreezeBalance(accountId, amount)) {
			log.error("unfreeze balance error . accountId {}", accountId);
			throw new DepositException(DepositResultCode.FAIL, "解冻失败！");
		}
	}

	public void debit(String accountId, BigDecimal amount) {
		if (this.accountMapper.debit(accountId, amount) != 1) {
			log.error("debit operation error,accountId {}", accountId);
			throw new DepositException(DepositResultCode.FAIL, "下账失败");
		}
	}

	public void credit(String accountId, BigDecimal amount) {
		if (this.accountMapper.credit(accountId, amount) != 1) {
			log.error("credit operation error,accountId {}", accountId);
			throw new DepositException(DepositResultCode.FAIL, "上账失败");
		}
	}

}
