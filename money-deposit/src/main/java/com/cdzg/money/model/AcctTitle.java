package com.cdzg.money.model;

import java.io.Serializable;

import com.cdzg.money.model.enums.DrCr;
import com.cdzg.money.model.enums.TitleScopeTypeEnum;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AcctTitle implements Serializable {

	private static final long serialVersionUID = -4046434136006022758L;

	private Long id;
	private Currency currency;
	private String acctTitleName;
	// 科目编号
	private String acctTitleCode;
	// 范围 内部 外部
	private TitleScopeTypeEnum scopeTypeEnum;
	// 是否为开户科目
	private boolean hasAccount;
	// 余额方向
	private DrCr balanceDirectionEnum;

}
