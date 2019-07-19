package com.cdzg.money.mapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.cdzg.money.mapper.entity.AccountPO;

@Mapper
public interface AccountMapper {

	void save(AccountPO account);

	AccountPO findByAccountId(@Param("acctId") String acctId);

	List<AccountPO> listByMemberId(@Param("memberId") String memberId);

	int updateStatusTo(@Param("accountId") String accountId, @Param("accountStatus") int status);

	List<AccountPO> selectByIdSet(@Param("list") Set<String> idList);

	/**
	 * 冻结
	 * 
	 * @param accountId
	 * @param amount
	 */
	int freezeBalance(@Param("accountId") String accountId, @Param("amount") BigDecimal amount);

	/**
	 * 解冻
	 * 
	 * @param accountId
	 * @param amount
	 */
	int unfreezeBalance(@Param("accountId") String accountId, @Param("amount") BigDecimal amount);

	int debit(@Param("accountId") String accountId, @Param("amount") BigDecimal amount);

	int credit(@Param("accountId") String accountId, @Param("amount") BigDecimal amount);

}
