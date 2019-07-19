package com.cdzg.money.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.cdzg.money.mapper.entity.PaymentRequestPO;

@Mapper
public interface PaymentRequestMapper {

	void save(PaymentRequestPO paymentOrder);

	PaymentRequestPO findByBizNo(@Param("bizNo") String bizNo, @Param("appId") String appId);

}
