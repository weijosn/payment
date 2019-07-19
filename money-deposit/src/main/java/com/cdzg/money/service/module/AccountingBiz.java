package com.cdzg.money.service.module;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cdzg.money.mapper.AccountDCMapper;
import com.cdzg.money.mapper.entity.AccountDcPO;
import com.cdzg.money.model.AccountingEntry;
import com.cdzg.money.model.Currency;
import com.cdzg.money.model.PairAccountSuite;
import com.cdzg.money.model.enums.DrCr;
import com.cdzg.money.repository.AccountRepository;

@Component
public class AccountingBiz {

    @Autowired
    private AccountDCMapper accountDCMapper;

    @Autowired
    private AccountRepository accountRepository;

    public void doPairSuites(AccountingEntry creditAccount, String suiteNo) {
        AccountDcPO debit = buildDC(creditAccount, creditAccount.getDepositAccount().getCurrency(), suiteNo, new Date());
        debit.setDcDirection(DrCr.Credit.getCode());
        accountDCMapper.save(debit);
        accountRepository.credit(creditAccount.getDepositAccount().getAccountNo(), creditAccount.getAmount());
    }

    public void doPairSuites(List<PairAccountSuite> suites) {

        // 无借贷指令
        if (suites.isEmpty()) {
            return;
        }

        // 存借贷记录
        List<AccountDcPO> list = new ArrayList<AccountDcPO>();
        suites.stream().forEach(value -> {
            list.addAll(buildDC(value));
        });
        accountDCMapper.batchSave(list);

        // 操作账户
        list.forEach(value -> {
            switch (DrCr.getByCode(value.getDcDirection())) {
                case Debit:
                    accountRepository.debit(value.getAccountId(), value.getAmount());
                    break;
                case Credit:
                    accountRepository.credit(value.getAccountId(), value.getAmount());
                    break;
            }
        });
    }

    private List<AccountDcPO> buildDC(PairAccountSuite value) {

        AccountingEntry debitAccount = value.getDebitEntry();
        AccountingEntry creditAccount = value.getCreditEntry();
        Date settleTime = value.getSettlementTime();

        AccountDcPO credit = buildDC(creditAccount, value.getCurrency(), value.getSuiteNo(), settleTime);
        credit.setDcDirection(DrCr.Credit.getCode());

        AccountDcPO debit = buildDC(debitAccount, value.getCurrency(), value.getSuiteNo(), settleTime);
        debit.setDcDirection(DrCr.Debit.getCode());

        return Arrays.asList(debit, credit);
    }

    private AccountDcPO buildDC(AccountingEntry creditAccount, Currency currency, String suiteNo, Date settleTime) {
        AccountDcPO dc = new AccountDcPO();
        dc.setAccountId(creditAccount.getDepositAccount().getAccountNo());
        dc.setAccountTitleId(creditAccount.getDepositAccount().getAcctTitle().getId());
        dc.setAmount(creditAccount.getAmount());
        dc.setBalance(creditAccount.getDepositAccount().getBalance());
        dc.setCurrencyType(currency.getCurrencyType());
        dc.setInsertTime(new Date());
        dc.setSuiteNo(suiteNo);
        dc.setSummary(creditAccount.getMemo());
        dc.setSettleTime(settleTime);
        return dc;
    }

}
