package com.cdzg.money.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.cdzg.money.mapper.entity.AccountDcPO;

@Mapper
public interface AccountDCMapper {

	void batchSave(@Param("list") List<AccountDcPO> buildDC);

	void save(@Param("item") AccountDcPO debit);

}
