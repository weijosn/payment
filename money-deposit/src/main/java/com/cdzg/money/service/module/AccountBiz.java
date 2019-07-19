package com.cdzg.money.service.module;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.cdzg.money.api.deposit.vo.DepositBaseResponse;
import com.cdzg.money.api.deposit.vo.account.AccountTypeEnum;
import com.cdzg.money.api.deposit.vo.account.AccountVO;
import com.cdzg.money.api.deposit.vo.account.AcctBatchCreationRequest;
import com.cdzg.money.api.deposit.vo.account.AcctBatchCreationRequest.AccountInfo;
import com.cdzg.money.api.deposit.vo.account.AcctCreationRequest;
import com.cdzg.money.api.deposit.vo.account.AcctStatusChangeRequest;
import com.cdzg.money.api.deposit.vo.account.AcctsByMemberQueryRequest;
import com.cdzg.money.api.deposit.vo.account.AcctsQueryResponse;
import com.cdzg.money.exception.DepositResultCode;
import com.cdzg.money.exception.ExceptionUtils;
import com.cdzg.money.model.Account;
import com.cdzg.money.model.AcctTitle;
import com.cdzg.money.model.AcctountTileKey;
import com.cdzg.money.model.Currency;
import com.cdzg.money.model.enums.TitleScopeTypeEnum;
import com.cdzg.money.repository.AccountRepository;
import com.cdzg.money.repository.AccountTitleRepository;
import com.cdzg.money.repository.CurrencyRepository;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AccountBiz {

    @Autowired
    private AccountTitleRepository acctTitleRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private CurrencyRepository currencyRepository;

    public String createAcct(AcctCreationRequest request) {

        final Currency currency = currencyRepository.findByCode(request.getCurrencyCode());
        Assert.isTrue(currency != null, "currency[" + currency.getCurrencyCode() + "]not exist");

        final AcctountTileKey titleKey = AcctountTileKey.instance(request.getAccountTitleCode(), currency.getCurrencyType());
        final AcctTitle acctTitle = acctTitleRepository.queryByPrimaryKey(titleKey);
        Assert.isTrue(acctTitle != null, "Title[" + titleKey.getAccTitleCode() + "] currency[" + currency + "]not exist");

        AccountBase base = new AccountBase();
        base.setAccountName(request.getAccountName());
        base.setMemberId(request.getMemberId());
        base.setMemo(request.getMemo());
        base.generateAccountNo(request.getAccountType(), acctTitle.getAcctTitleCode(), currency.getCurrencyType());

        return _newAcct(currency, acctTitle, request.getAccountType(), base);
    }

    public Map<String, String> createAcct(AcctBatchCreationRequest request) {

        final Currency currency = currencyRepository.findByCode(request.getCurrencyCode());
        Assert.isTrue(currency != null, "currency[" + currency.getCurrencyCode() + "]not exist");

        final AcctountTileKey titleKey = AcctountTileKey.instance(request.getAccountTitleCode(), currency.getCurrencyType());
        final AcctTitle acctTitle = acctTitleRepository.queryByPrimaryKey(titleKey);
        Assert.isTrue(acctTitle != null, "Title[" + titleKey.getAccTitleCode() + "] currency[" + currency + "]not exist");

        ArrayList<AccountBase> list = Lists.newArrayList();
        request.getAccounts().forEach(value -> {
            AccountBase base = new AccountBase();
            list.add(base.instance(value).generateAccountNo(request.getAccountType(), acctTitle.getAcctTitleCode(),
                    currency.getCurrencyType()));
        });

        Map<String, String> result = new HashMap<String, String>();
        // 批量开户，仅返回成功的数据
        for (AccountBase base : list) {
            try {
                result.put(base.getMemberId(), _newAcct(currency, acctTitle, request.getAccountType(), base));
            } catch (Exception e) {
                log.error("open account error.", e);
            }
        }
        return result;
    }

    private String _newAcct(Currency currency, AcctTitle acctTitle, AccountTypeEnum accType, AccountBase accountBase) {

        Assert.isTrue(acctTitle.getScopeTypeEnum() == TitleScopeTypeEnum.Internal,
                "title:[" + acctTitle.getId() + "] is not internal title");
        final String acctId = accountBase.getAccountNo();
        // 生成accountId
        try {

            Account account = new Account();
            account.setAccountNo(accountBase.getAccountNo());
            account.setCurrency(currency);
            account.setAcctTitle(acctTitle);
            account.setAccountType(accType);
            account.setAccountName(accountBase.getAccountName());
            account.setMemberId(accountBase.getMemberId());
            account.setMemo(StringUtils.defaultString(accountBase.getMemo(), StringUtils.EMPTY));
            // 入库，通过数据库唯一索引来保证
            accountRepository.save(account);
            return account.getAccountNo();
        } catch (DataAccessException dive) {
            /**
             * 如果是违反唯一性约束的，则表明当前科目下面已经开了这个账号。 返回已经存在的账号
             */
            if (ExceptionUtils.isDuplicate(dive)) {
                log.warn("save internal account Duplicate error,accountId {}", acctId);
                Account orgiAcctId = accountRepository.findByAccountId(acctId);
                if (orgiAcctId == null) {
                    throw new RuntimeException(
                            "titleId[" + acctTitle.getAcctTitleCode() + "], currency[" + currency.getCurrencyCode()
                                    + "]" + "can't open internal account, conflict with existing internal account");
                }
                return orgiAcctId.getAccountNo();
            } else {
                throw dive;
            }
        }
    }

    public AccountVO findByAccountId(String accountId) {
        Account account = accountRepository.findByAccountId(accountId);
        Assert.isTrue(account != null, "指定的账号不存在");
        return toAccountVO(account);
    }

    public AcctsQueryResponse queryAcctsByMember(AcctsByMemberQueryRequest request) {
        AcctsQueryResponse rep = new AcctsQueryResponse();
        rep.setAccounts(new ArrayList<AccountVO>(5));
        try {
            accountRepository.queryAcctsByMemberId(request.getMemberId()).forEach(value -> {
                rep.getAccounts().add(toAccountVO(value));
            });
            rep.setResultCode(DepositResultCode.SUCCESS.getCode());
            rep.setResultMessage(DepositResultCode.SUCCESS.getMessage());
        } catch (java.lang.IllegalArgumentException e) {
            log.warn("query accts illegal argument , {}", e.getMessage());
            rep.setResultCode(DepositResultCode.ILLEGAL_ARGUMENT.getCode());
            rep.setResultMessage(e.getMessage());
        } catch (Exception e) {
            log.error("query accts exception.", e);
            rep.setResultCode(DepositResultCode.FAIL.getCode());
            rep.setResultMessage(DepositResultCode.FAIL.getMessage());
        }
        return rep;
    }

    private AccountVO toAccountVO(Account value) {
        AccountVO vo = new AccountVO();
        vo.setAccountNo(value.getAccountNo());
        vo.setAccountName(value.getAccountName());
        vo.setAccountType(AccountTypeEnum.getByCode(value.getAccountType().getCode()));
        vo.setBalance(new AccountVO.Balance(value.getBalance(), value.getAvailablebalance(), value.getFrozenbalance()));
        vo.setCurrencyCode(value.getCurrency().getCurrencyCode());
        vo.setMemberId(value.getMemberId());
        vo.setStatus(value.getStatus());

        value.getAcctTitle().getAcctTitleName();

        return vo;
    }

    public DepositBaseResponse changeAcctStatus(AcctStatusChangeRequest request) {
        DepositBaseResponse rep = new DepositBaseResponse();
        if (accountRepository.changeAcctStatus(request.getAccountId(), request.getTargetStatus())) {
        	rep.setResultCode(DepositResultCode.SUCCESS.getCode());
			rep.setResultMessage(DepositResultCode.SUCCESS.getCode());
        } else {
            rep.setResultCode(DepositResultCode.FAIL.getCode());
            rep.setResultMessage(DepositResultCode.FAIL.getMessage());
        }
        return rep;
    }

    @Getter
    @Setter
    class AccountBase {
        String memberId, accountName, memo;
        String accountNo;

        AccountBase instance(AccountInfo accountInfo) {
            memberId = accountInfo.getMemberId();
            accountName = accountInfo.getAccountName();
            memo = accountInfo.getMemo();
            return this;
        }

        AccountBase generateAccountNo(AccountTypeEnum accountType, String titleId, int currencyType) {
            StringBuilder builder = new StringBuilder(accountType.getCode());
            builder.append(AccountTypeEnum.Internal.getCode());
            builder.append(titleId);
            builder.append(StringUtils.leftPad(String.valueOf(currencyType), 3, "0"));
            builder.append(StringUtils.leftPad(memberId, 10, "0"));
            accountNo = builder.toString();
            return this;
        }

    }

}
