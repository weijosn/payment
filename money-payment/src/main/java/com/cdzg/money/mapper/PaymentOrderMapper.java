package com.cdzg.money.mapper;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.cdzg.money.mapper.entity.PaymentOrderPO;

@Mapper
public interface PaymentOrderMapper {

	void save(PaymentOrderPO paymentOrder);

	PaymentOrderPO findByTradeNo(@Param("tradeNo") String tradeNo, @Param("appId") String appId);

	int updateToSucced(@Param("id") long id, @Param("paiedAmount") BigDecimal paiedTotalAmount,
			@Param("paymentTime") Date paymentTime);

	int updateToPartyPaied(@Param("id") long id, @Param("partyPaiedTotalAmount") BigDecimal partyPaiedTotalAmount,
			@Param("paymentTime") Date paymentTime);

	PaymentOrderPO findByOrderId(@Param("orderId") String orderId);

	int closeOrder(@Param("orderId")String orgPaymentOrderId);

}
