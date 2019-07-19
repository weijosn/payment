package com.cdzg.money;

import java.math.BigDecimal;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.alibaba.dubbo.config.annotation.Reference;
import com.cdzg.money.api.payment.request.PartyInfo;
import com.cdzg.money.api.payment.request.PaymentOrderInfo;
import com.cdzg.money.api.payment.request.PaymentPartyInfo;
import com.cdzg.money.api.payment.request.PaymentRequest;
import com.cdzg.money.api.payment.request.PaymentResult;
import com.cdzg.money.api.service.payment.IPaymentService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TradeTest {

	@Reference(version = "1.0.0", check = false, timeout = 999999999, retries = -1)
	private IPaymentService paymentService;

	@Test
	public void trade() throws Exception {
		PaymentResult rep = this.paymentService.payment(buildPaymentRequest());
		System.out.println(rep);
	}

	private PaymentRequest buildPaymentRequest() {
		String bizNo = "";
		bizNo = String.valueOf(System.currentTimeMillis());
		PaymentRequest pr = new PaymentRequest();
		pr.setAppId("money-payment");
		pr.setTradeNo(bizNo);
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
//		pi.setCouponsAmount(BigDecimal.valueOf(22));
//		pi.setIndividualAmount(BigDecimal.valueOf(18));
//		pi.setUnionAmount(BigDecimal.valueOf(10));
		pi.setGoodsName("goodsName");
		pi.setPayRemark("test_pay");
		pi.setNotifyurl("http://www.baidu.com/");
		//pi.setPayAmount(pi.getUnionAmount().add(pi.getIndividualAmount()).add(pi.getCouponsAmount()));
		pi.setPayAmount(BigDecimal.valueOf(1));
		return pi;
	}

}