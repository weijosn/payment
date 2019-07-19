package com.cdzg.money.repository;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.cdzg.money.mapper.CurrencyMappingMapper;
import com.cdzg.money.mapper.entity.CurrencyMappingPO;
import com.cdzg.money.model.Currency;

@Component
public class CurrencyRepository {

	@Autowired
	private CurrencyMappingMapper currencyMappingMapper;

	/**
	 * 根据编码获取币种
	 * 
	 * @param code
	 * @return
	 */
	public Currency findByCode(String code) {
		Assert.isTrue(!StringUtils.isEmpty(code), "currency code is empty!");
		CurrencyMappingPO currency = currencyMappingMapper.findByCode(StringUtils.trim(code));
		return toCurrency(currency);
	}
	
	public Currency findByType(int type) {
		CurrencyMappingPO currency = currencyMappingMapper.findByType(type);
		return toCurrency(currency);
	}

	private Currency toCurrency(CurrencyMappingPO currency) {
		Assert.isTrue(currency != null, "currency object is NULL!");
		Currency c = new Currency();
		c.setCurrencyCode(currency.getCurrencyCode());
		c.setCurrencyName(currency.getCurrencyName());
		c.setCurrencyType(currency.getCurrencyType());
		return c;
	}

}
