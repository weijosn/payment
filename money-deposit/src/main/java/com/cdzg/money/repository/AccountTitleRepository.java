package com.cdzg.money.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cdzg.money.mapper.AccountTitleMapper;
import com.cdzg.money.mapper.entity.AccountTitlePO;
import com.cdzg.money.model.AcctTitle;
import com.cdzg.money.model.AcctountTileKey;
import com.cdzg.money.model.enums.DrCr;
import com.cdzg.money.model.enums.TitleScopeTypeEnum;

@Component
public class AccountTitleRepository {

	@Autowired
	private AccountTitleMapper accountTitleMapper;
	@Autowired
	private CurrencyRepository currencyRepository;

	public AcctTitle queryByPrimaryKey(AcctountTileKey titleKey) {
		return toAcctTitle(this.accountTitleMapper.findByPrimaryKey(titleKey));
	}

	public AcctTitle toAcctTitle(AccountTitlePO act) {
		AcctTitle at = new AcctTitle();
		at.setId(act.getId());
		at.setAcctTitleName(act.getAccountTitleName());
		at.setBalanceDirectionEnum(DrCr.getByCode(act.getBalanceDirection()));
		at.setCurrency(currencyRepository.findByType(act.getCurrencyType()));
		at.setScopeTypeEnum(TitleScopeTypeEnum.getByCode(act.getScopeType()));
		at.setAcctTitleCode(act.getAccountTitleCode());
		return at;
	}

	public AcctTitle findById(Long accountTitleId) {
		return toAcctTitle(this.accountTitleMapper.findById(accountTitleId));
	}

}
