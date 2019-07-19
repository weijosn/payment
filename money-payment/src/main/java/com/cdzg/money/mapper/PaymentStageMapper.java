package com.cdzg.money.mapper;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.cdzg.money.mapper.entity.PaymentStagePO;

@Mapper
public interface PaymentStageMapper {

	void batchSave(@Param("list") List<PaymentStagePO> stages);

	List<PaymentStagePO> listByOrderId(@Param("orderId") String orderId);

	PaymentStagePO findById(@Param("id") long stageId);

	int updateToInit(@Param("id") long stageId, @Param("oldStatus") String oldStatus);

	int updateToPending(@Param("id") long stageId, @Param("readlyTime") Date date,
			@Param("oldStatus") String oldStatus);

	int updateToSucced(@Param("id") long stageId, @Param("paiedAmount") BigDecimal paiedAmount,
			@Param("paymentTime") Date date, @Param("oldStatus") String oldStatus);

}
