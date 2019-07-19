package com.cdzg.money.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.cdzg.money.mapper.entity.AccountTitlePO;
import com.cdzg.money.model.AcctountTileKey;

@Mapper
public interface AccountTitleMapper {

	@Select("select * from tb_account_title where currency_type=#{currencyType} and account_title_code=#{accTitleCode}")
	AccountTitlePO findByPrimaryKey(AcctountTileKey titleKey);

	@Select("select * from tb_account_title where id = #{id}")
	AccountTitlePO findById(@Param("id")Long accountTitleId);

}
