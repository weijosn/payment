package com.cdzg.money;

import com.alibaba.dubbo.config.annotation.Reference;
import com.cdzg.money.api.payment.request.PartyInfo;
import com.cdzg.money.api.payment.request.PaymentOrderInfo;
import com.cdzg.money.api.payment.request.PaymentPartyInfo;
import com.cdzg.money.api.payment.request.PaymentRequest;
import com.cdzg.money.api.service.payment.IPaymentService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PayTest {

	@Reference(version = "1.0.0", check = false, timeout = 999999999, retries = -1)
	private IPaymentService paymentService;

	@Test
	public void pay() throws Exception {
		this.paymentService.fundIn(buildPaymentRequest());
	}

	private PaymentRequest buildPaymentRequest() {
		PaymentRequest pr = new PaymentRequest();
		pr.setAppId("money-payment");
		pr.setTradeNo(String.valueOf(System.currentTimeMillis()));
		pr.setPaymentOrderInfo(buildPaymentOrderInfo());
		pr.setPaymentPartyInfo(buildPaymentParty());
		return pr;
	}

	private PaymentPartyInfo buildPaymentParty() {
		PaymentPartyInfo pp = new PaymentPartyInfo();
		pp.setPayer(PartyInfo.instance("3103001103583100096753664", "1908817000118536"));
		pp.setPayee(PartyInfo.instance("3103001103539592325509120", "1908817000118535"));
		pp.setBizInitiator(pp.getPayer());
		return pp;
	}

	private PaymentOrderInfo buildPaymentOrderInfo() {
		PaymentOrderInfo pi = new PaymentOrderInfo();
		pi.setGoodsName("goodsName");
		pi.setPayRemark("test_pay");
		pi.setPayAmount(BigDecimal.valueOf(1));
		return pi;
	}

}