package com.cdzg.money.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.cdzg.money.api.deposit.vo.settle.DebitCreditAccounting;
import com.cdzg.money.api.deposit.vo.settle.FreezeAccounting;
import com.cdzg.money.api.deposit.vo.settle.SettlementRequest;
import com.cdzg.money.api.deposit.vo.settle.TargetAccount;
import com.cdzg.money.api.deposit.vo.settle.UnfreezeAccounting;
import com.cdzg.money.exception.DepositResultCode;
import com.cdzg.money.exception.DepositException;
import com.cdzg.money.model.enums.DrCr;
import com.cdzg.money.repository.AccountRepository;
import com.cdzg.money.repository.CurrencyRepository;
import com.cdzg.money.service.module.FreezeBuilderBiz;

@Component
@Scope("prototype")
public class SettlementInstruction {

	private List<UnfreezeInstruction> unfreezeInstructions = new ArrayList<UnfreezeInstruction>();

	private List<PairAccountSuite> suites = new ArrayList<PairAccountSuite>();

	private List<FreezeInstruction> freezeInstructions = new ArrayList<FreezeInstruction>();

	private Map<String, Account> beingUsedAccounts = new HashMap<String, Account>();

	@Autowired
	private FreezeBuilderBiz freezeBuilder;
	@Autowired
	private CurrencyRepository currencyRepository;
	@Autowired
	private AccountRepository accountRepository;

	public void buildAndValidate(SettlementRequest settlementRequest) {

		build(settlementRequest);

		validateTransRule();

	}

	private void build(SettlementRequest settlementRequest) {
		// 查询出全部需要的账户信息
		beingUsedAccounts = accountRepository.listByAccountIds(getBeingUsedAccountIds(settlementRequest));
		this.setUnfreezeInstructions(freezeBuilder.buildUnfreezeInstructions(settlementRequest.getUnfreezeAccountings(), beingUsedAccounts));
		this.setSuites(buildSuites(settlementRequest.getDebitCreditAccountings(), beingUsedAccounts));
		this.setFreezeInstructions(freezeBuilder.buildFreezeInstructions(settlementRequest.getFreezeAccountings(), beingUsedAccounts));
	}

	private List<PairAccountSuite> buildSuites(List<DebitCreditAccounting> debitCreditAccountings,
			Map<String, Account> beingUsedAccountIds) {
		List<PairAccountSuite> suites = new ArrayList<PairAccountSuite>();
		if (debitCreditAccountings != null && !debitCreditAccountings.isEmpty()) {
			for (DebitCreditAccounting debitCreditAccounting : debitCreditAccountings) {
				PairAccountSuite drCrAccountSuite = new PairAccountSuite();
				drCrAccountSuite.setSuiteNo(debitCreditAccounting.getSuiteNo());
				drCrAccountSuite.setSettlementTime(debitCreditAccounting.getSettlementTime());
				Currency currency = currencyRepository.findByCode(debitCreditAccounting.getCurrencyCode());
				drCrAccountSuite.setCurrency(currency);
				buildAccountingEntry(drCrAccountSuite, debitCreditAccounting, DrCr.Debit, beingUsedAccountIds);
				buildAccountingEntry(drCrAccountSuite, debitCreditAccounting, DrCr.Credit, beingUsedAccountIds);
				suites.add(drCrAccountSuite);
			}
		}
		return suites;
	}

	private void buildAccountingEntry(PairAccountSuite drCrAccountSuite, DebitCreditAccounting debitCreditAccounting,
			DrCr drCr, Map<String, Account> beingUsedAccountIds) {
		TargetAccount targetAccount = DrCr.Credit.equals(drCr) ? debitCreditAccounting.getCreditAccount()
				: debitCreditAccounting.getDebitAccount();
		Account depositAccount = getDepositAccountById(beingUsedAccountIds, targetAccount.getAccountId());
		AccountingEntry accountingEntry = DrCr.Credit.equals(drCr) ? drCrAccountSuite.getCreditEntry()
				: drCrAccountSuite.getDebitEntry();
		accountingEntry.setDepositAccount(depositAccount);
		accountingEntry.setDrCr(drCr);
		accountingEntry.setMemo(targetAccount.getSummary());
		accountingEntry.setAmount(debitCreditAccounting.getAmount());
	}

	private Account getDepositAccountById(Map<String, Account> beingUsedAccountIds, String accountId) {
		Account depositAccount = beingUsedAccountIds.get(accountId);
		if (depositAccount == null) {
			throw new IllegalArgumentException("the accountId [" + accountId + "] does not exist");
		}
		return depositAccount;
	}

	private Set<String> getBeingUsedAccountIds(SettlementRequest settlementRequest) {
		Set<String> accountIds = new HashSet<String>();
		List<UnfreezeAccounting> unfreezeAccountings = settlementRequest.getUnfreezeAccountings();
		if (unfreezeAccountings != null && !unfreezeAccountings.isEmpty()) {
			for (UnfreezeAccounting unfreezeAccounting : unfreezeAccountings) {
				accountIds.add(unfreezeAccounting.getTargetAccount().getAccountId());
			}
		}
		List<DebitCreditAccounting> debitCreditAccountings = settlementRequest.getDebitCreditAccountings();
		if (debitCreditAccountings != null && !debitCreditAccountings.isEmpty()) {
			for (DebitCreditAccounting debitCreditAccounting : debitCreditAccountings) {
				accountIds.add(debitCreditAccounting.getCreditAccount().getAccountId());
				accountIds.add(debitCreditAccounting.getDebitAccount().getAccountId());
			}
		}
		List<FreezeAccounting> freezeAccountings = settlementRequest.getFreezeAccountings();
		if (freezeAccountings != null && !freezeAccountings.isEmpty()) {
			for (FreezeAccounting freezeAccounting : freezeAccountings) {
				accountIds.add(freezeAccounting.getTargetAccount().getAccountId());
			}
		}
		return accountIds;
	}

	private void validateTransRule() {
		validateSuites();
		validateBalance();
	}

	private void validateSuites() {
		List<PairAccountSuite> suites = this.getSuites();
		for (PairAccountSuite drCrAccountSuite : suites) {
			AccountingEntry creditEntry = drCrAccountSuite.getCreditEntry();
			AccountingEntry debitEntry = drCrAccountSuite.getDebitEntry();

			debitEntry.validateAccountStatus();
			creditEntry.validateAccountStatus();
			
			if (debitEntry.getDepositAccount().getCurrency().getCurrencyType() != creditEntry.getDepositAccount()
					.getCurrency().getCurrencyType()) {
				throw new IllegalArgumentException(
						"the debit/credit account currency mismatches, id[" + drCrAccountSuite.getSuiteNo() + "]");
			}
			if (debitEntry.getDepositAccount().getCurrency().getCurrencyType() != drCrAccountSuite.getCurrency()
					.getCurrencyType()) {
				throw new IllegalArgumentException("the request’s money currency mismatches account currency, id["
						+ drCrAccountSuite.getSuiteNo() + "]");
			}
		}
	}

	private void validateBalance() {
		/**
		 * key:DepositAccount, Value:做完当前指令后的可用余额
		 */
 		Map<Account, BigDecimal> balanceMap = new HashMap<Account, BigDecimal>();
		for (UnfreezeInstruction unfreezeInstruction : this.getUnfreezeInstructions()) {
			this.addToBalanceMap(unfreezeInstruction.getDepositAccount(), balanceMap, unfreezeInstruction.getAmount());
		}
		for (PairAccountSuite pairAccountSuite : this.getSuites()) {
			addAccountingEntryToBalanceMap(pairAccountSuite.getCreditEntry(), balanceMap);
			addAccountingEntryToBalanceMap(pairAccountSuite.getDebitEntry(), balanceMap);
		}
		for (FreezeInstruction freezeInstruction : this.getFreezeInstructions()) {
			this.addToBalanceMap(freezeInstruction.getDepositAccount(), balanceMap,
					freezeInstruction.getAmount().negate());
		}
	}

	private void addAccountingEntryToBalanceMap(AccountingEntry accountingEntry, Map<Account, BigDecimal> balanceMap) {
		BigDecimal amount = accountingEntry.getAmount();
		if (accountingEntry.getDrCr().equals(DrCr.Debit)) {
			amount = amount.negate();
		}
		this.addToBalanceMap(accountingEntry.getDepositAccount(), balanceMap, amount);
	}

	private void addToBalanceMap(Account depositAccount, Map<Account, BigDecimal> balanceMap, BigDecimal amount) {
		BigDecimal remainingAvailAmount = depositAccount.getAvailablebalance();
		if (balanceMap.containsKey(depositAccount)) {
			remainingAvailAmount = balanceMap.get(depositAccount);
		}
		remainingAvailAmount = remainingAvailAmount.add(amount);
		if (remainingAvailAmount.compareTo(BigDecimal.ZERO) < 0) {
			throw new DepositException(DepositResultCode.ACCOUNT_BALANCE_NOT_ENOUGH,
					"accountId[" + depositAccount.getAccountNo() + "], available balance is not enough");
		}
		balanceMap.put(depositAccount, remainingAvailAmount);
	}

	public List<UnfreezeInstruction> getUnfreezeInstructions() {
		return unfreezeInstructions;
	}

	public void setUnfreezeInstructions(List<UnfreezeInstruction> unfreezeInstructions) {
		this.unfreezeInstructions = unfreezeInstructions;
	}

	public List<FreezeInstruction> getFreezeInstructions() {
		return freezeInstructions;
	}

	public void setFreezeInstructions(List<FreezeInstruction> freezeInstructions) {
		this.freezeInstructions = freezeInstructions;
	}

	public List<PairAccountSuite> getSuites() {
		return suites;
	}

	public void setSuites(List<PairAccountSuite> suites) {
		this.suites = suites;
	}

	public Map<String, Account> getBeingUsedAccounts() {
		return beingUsedAccounts;
	}

	public void setBeingUsedAccounts(Map<String, Account> beingUsedAccounts) {
		this.beingUsedAccounts = beingUsedAccounts;
	}


}
