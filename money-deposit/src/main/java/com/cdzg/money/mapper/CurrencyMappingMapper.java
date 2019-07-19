package com.cdzg.money.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.cdzg.money.mapper.entity.CurrencyMappingPO;

@Mapper
public interface CurrencyMappingMapper {

	@Select("select * from tb_currency_mapping where currency_code = #{code}")
	CurrencyMappingPO findByCode(@Param("code") String code);

	@Select("select * from tb_currency_mapping where currency_type = #{type}")
	CurrencyMappingPO findByType(@Param("type") int type);

}
