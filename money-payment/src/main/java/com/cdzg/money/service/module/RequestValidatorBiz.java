package com.cdzg.money.service.module;

import java.math.BigDecimal;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.cdzg.money.api.payment.request.PaymentRequest;
import com.cdzg.money.api.refund.request.RefundRequest;
import com.cdzg.money.mapper.PaymentOrderMapper;
import com.cdzg.money.mapper.entity.PaymentOrderPO;
import com.cdzg.money.model.enums.PaymentCodeEnum;

/**
 * <p>
 * 退款请求验证器
 **/
@Component
public class RequestValidatorBiz {

	@Autowired
	private PaymentOrderMapper paymentMapper;

	public void validate(RefundRequest request) {
		Assert.notNull(request, "请求对象为空");
		Assert.notNull(request, "业务信息为空");
		Assert.hasLength(request.getPaymentOrderId(), "支付业务订单号不能为空");
		Assert.hasLength(request.getTradeNo(), "支付交易请求号不正确");
		Assert.hasLength(request.getAppId(), "APPID不正确");
		Assert.isTrue(null != request.getRefundAmount() && request.getRefundAmount().compareTo(BigDecimal.ZERO) > 0,
				"退款金额不正确");
		PaymentOrderPO order = paymentMapper.findByOrderId(request.getPaymentOrderId());
		Assert.isTrue(order != null, "指定的支付订单不存在");
	}

	public void validate(PaymentRequest request, PaymentCodeEnum paymentCode) {
		
		Assert.hasLength(request.getAppId(), "appId 不能为空");
		Assert.hasLength(request.getTradeNo(), "tradeNo不能为空");
		
		Assert.isTrue(request.getPaymentOrderInfo() != null, "paymentOrderInfo不能为空");
		Assert.isTrue(request.getPaymentPartyInfo() != null, "paymentPartyInfo不能为空");
		
		if (paymentCode == PaymentCodeEnum.REFUND) {
			Assert.isTrue(StringUtils.isNotEmpty(request.getPaymentOrderInfo().getOrgPaymentOrderId()), "原始订单号不能为空");
		}
		
	}

}
