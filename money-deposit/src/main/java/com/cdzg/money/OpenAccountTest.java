package com.cdzg.money;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.alibaba.dubbo.config.annotation.Reference;
import com.cdzg.money.api.deposit.vo.account.AccountTypeEnum;
import com.cdzg.money.api.deposit.vo.account.AcctCreationRequest;
import com.cdzg.money.api.deposit.vo.account.AcctsByMemberQueryRequest;
import com.cdzg.money.api.deposit.vo.settle.DebitCreditAccounting;
import com.cdzg.money.api.deposit.vo.settle.FreezeAccounting;
import com.cdzg.money.api.deposit.vo.settle.SettlementRequest;
import com.cdzg.money.api.deposit.vo.settle.TargetAccount;
import com.cdzg.money.api.deposit.vo.settle.UnfreezeAccounting;
import com.cdzg.money.api.service.account.IAccountService;
import com.cdzg.money.api.service.settle.ISettlementService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OpenAccountTest {


    @Reference(version = "1.0.0", check = false, timeout = 999999999, retries = -1)
    private IAccountService accountService;
    @Reference(version = "1.0.0", check = false, timeout = 999999999, retries = -1)
    private ISettlementService settlementService;

    @Test
    public void openAccount() throws Exception {

        AcctCreationRequest request = new AcctCreationRequest();
        request.setAccountName("中金会员待清算户");
        request.setAccountTitleCode("103");
        request.setAppId("1");
        request.setBizNo(String.valueOf(System.currentTimeMillis()));
        request.setCurrencyCode("CNY");
        //103539592325509120
        //103583100096753664
        //request.setMemberId("103539592325509120");
        request.setMemberId("6358687917375750295");
        request.setAccountType(AccountTypeEnum.Individual);
        request.setTargetInstCode("111");
        accountService.createAcct(request);

        AcctsByMemberQueryRequest qry = new AcctsByMemberQueryRequest();
        qry.setAppId("1");
        qry.setBizNo(String.valueOf(System.currentTimeMillis()));
        qry.setMemberId("1552574870787");
        qry.setSubAccountTypes(null);
        accountService.queryAcctsByMember(qry);

    }

    @Test
    public void settle() throws Exception {
        doSettle();
    }

    @Test
    public void freeze() throws Exception {
        doFreeze();
    }


    @Test
    public void unfreeze() throws Exception {
        //for (int i = 0; i < 10; i++) {
        doUnfreeze();
        //}
    }

    private void doUnfreeze() {

        SettlementRequest request = new SettlementRequest();
        request.setAppId("1");

        List<FreezeAccounting> freeze = new ArrayList<FreezeAccounting>();
        List<UnfreezeAccounting> unfreeze = new ArrayList<UnfreezeAccounting>();
        List<DebitCreditAccounting> accountings = new ArrayList<DebitCreditAccounting>();

        request.setBizNo(String.valueOf(System.currentTimeMillis()));

        {
            UnfreezeAccounting acc = new UnfreezeAccounting();
            acc.setAmount(BigDecimal.valueOf(4));
            acc.setCurrencyCode("CNY");
            acc.setSummary("出款解冻");
            acc.setOrigBizNo("11");

            TargetAccount freezeAccount = new TargetAccount();
            freezeAccount.setAccountId("031010011552574870787");
            freezeAccount.setSummary("收费");
            acc.setTargetAccount(freezeAccount);
            unfreeze.add(acc);
        }
        {
            UnfreezeAccounting acc = new UnfreezeAccounting();
            acc.setAmount(BigDecimal.valueOf(9));
            acc.setCurrencyCode("CNY");
            acc.setSummary("出款解冻");
            acc.setOrigBizNo("22");

            TargetAccount freezeAccount = new TargetAccount();
            freezeAccount.setAccountId("031010011552611065588");
            freezeAccount.setSummary("收费");
            acc.setTargetAccount(freezeAccount);
            unfreeze.add(acc);
        }

        request.setDebitCreditAccountings(accountings);
        request.setFreezeAccountings(freeze);
        request.setUnfreezeAccountings(unfreeze);

        settlementService.settle(request);
    }


    private void doFreeze() {

        SettlementRequest request = new SettlementRequest();
        request.setAppId("1");

        List<FreezeAccounting> freeze = new ArrayList<FreezeAccounting>();
        List<UnfreezeAccounting> unfreeze = new ArrayList<UnfreezeAccounting>();
        List<DebitCreditAccounting> accountings = new ArrayList<DebitCreditAccounting>();

        request.setBizNo(String.valueOf(System.currentTimeMillis()));

        {
            FreezeAccounting acc = new FreezeAccounting();
            acc.setAmount(BigDecimal.valueOf(10.11));
            acc.setCurrencyCode("CNY");
            acc.setOrigBizNo("11");
            acc.setSummary("出款冻结");

            TargetAccount freezeAccount = new TargetAccount();
            freezeAccount.setAccountId("031010011552574870787");
            freezeAccount.setSummary("收费");
            acc.setTargetAccount(freezeAccount);
            freeze.add(acc);
        }
        {
            FreezeAccounting acc = new FreezeAccounting();
            acc.setAmount(BigDecimal.valueOf(10.12));
            acc.setCurrencyCode("CNY");
            acc.setOrigBizNo("22");
            acc.setSummary("出款冻结");

            TargetAccount freezeAccount = new TargetAccount();
            freezeAccount.setAccountId("031010011552611065588");
            freezeAccount.setSummary("收费");
            acc.setTargetAccount(freezeAccount);
            freeze.add(acc);
        }

        request.setDebitCreditAccountings(accountings);
        request.setFreezeAccountings(freeze);
        request.setUnfreezeAccountings(unfreeze);

        settlementService.settle(request);
    }

    void doSettle() {

        SettlementRequest request = new SettlementRequest();
        request.setAppId("1");

        List<FreezeAccounting> freeze = new ArrayList<FreezeAccounting>();
        List<UnfreezeAccounting> unfreeze = new ArrayList<UnfreezeAccounting>();
        List<DebitCreditAccounting> accountings = new ArrayList<DebitCreditAccounting>();

        String suiteNo = String.valueOf(System.currentTimeMillis());

        {
            DebitCreditAccounting e = new DebitCreditAccounting();
            e.setAmount(BigDecimal.valueOf(20));
            {
                TargetAccount creditAccount = new TargetAccount();
                creditAccount.setAccountId("031010011552574870787");
                creditAccount.setSummary("付款");
                e.setCreditAccount(creditAccount);
            }
            e.setCurrencyCode("CNY");
            {
                TargetAccount debitAccount = new TargetAccount();
                debitAccount.setAccountId("031010011552611065588");
                debitAccount.setSummary("收款");
                e.setDebitAccount(debitAccount);
            }
            e.setSettlementTime(new Date());
            e.setSuiteNo(suiteNo);
            e.setSummary("无");

            accountings.add(e);
        }

        {
            FreezeAccounting acc = new FreezeAccounting();
            acc.setAmount(BigDecimal.valueOf(10.11));
            acc.setCurrencyCode("CNY");
            acc.setOrigBizNo("11");
            acc.setSummary("出款冻结");

            TargetAccount freezeAccount = new TargetAccount();
            freezeAccount.setAccountId("031010011552574870787");
            freezeAccount.setSummary("收费");
            acc.setTargetAccount(freezeAccount);
            freeze.add(acc);
        }

        request.setBizNo(String.valueOf(System.currentTimeMillis()));

        request.setDebitCreditAccountings(accountings);
        request.setFreezeAccountings(freeze);
        request.setUnfreezeAccountings(unfreeze);

        settlementService.settle(request);
    }

}