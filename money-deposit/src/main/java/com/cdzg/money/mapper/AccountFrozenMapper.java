package com.cdzg.money.mapper;

import java.math.BigDecimal;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.cdzg.money.mapper.entity.AccountFrozenPO;

@Mapper
public interface AccountFrozenMapper {

	AccountFrozenPO findByOrigBizNo(@Param("origBizNo") String origBizNo,@Param("accountId")String accountId);

	void saveFreeze(AccountFrozenPO accountFrozen);

	int unfreeze(@Param("origBizNo") String origBizNo,@Param("amount")BigDecimal amount);

}
