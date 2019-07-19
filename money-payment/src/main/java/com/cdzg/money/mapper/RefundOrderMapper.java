package com.cdzg.money.mapper;

import java.math.BigDecimal;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.cdzg.money.mapper.entity.RefundOrderPO;

@Mapper
public interface RefundOrderMapper {

	void save(RefundOrderPO refundOrder);

	RefundOrderPO findByPaymentOrderId(String paymentOrderId);

	int updateToPending(@Param("id") long id);

	int updateToSucced(@Param("id") long id, @Param("paiedAmount") BigDecimal refundAmount);

	int updateToFailed(@Param("id") long id);

}
