package com.cdzg.money.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.cdzg.money.mapper.entity.TransactionPO;

@Mapper
public interface TransactionMapper {

	void save(TransactionPO bean);

	TransactionPO findByPrimaryKey(@Param("bizNo") String bizNo, @Param("appId") String appId);

	int updateToStatus(@Param("bizNo")String requestNo,@Param("appId") String appId,@Param("newStatus") int newStatus,@Param("oldStatus") int oldStatus);

	int updateToSucced(@Param("bizNo")String requestNo,@Param("appId") String appId);

}
