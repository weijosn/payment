package com.cdzg.money.model;

import java.io.Serializable;

import com.cdzg.money.api.deposit.vo.account.AccountTypeEnum;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountType implements Serializable {

	private static final long serialVersionUID = 456374882325258215L;

	private AccountTypeEnum accountType;
	private int subAccountType;
	private String typeDesc;
	private String accountTitleId;
	private boolean unique;
	private boolean controlAccountTitle;
	private Currency currency;

}