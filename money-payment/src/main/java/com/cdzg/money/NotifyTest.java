package com.cdzg.money;

import java.math.BigDecimal;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.alibaba.dubbo.config.annotation.Reference;
import com.cdzg.money.api.notify.request.NotifyResult;
import com.cdzg.money.api.notify.request.PaymentNotifyRequest;
import com.cdzg.money.api.service.payment.IPaymentNotifyService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class NotifyTest {

	@Reference(version = "1.0.0", check = false, timeout = 999999999, retries = -1)
	private IPaymentNotifyService paymentNotifyService;

	@Test
	public void donotify() throws Exception {
		NotifyResult result = this.paymentNotifyService.notify(buildPaymentRequest());
		System.out.println(result);
	}

	private PaymentNotifyRequest buildPaymentRequest() {
		PaymentNotifyRequest pr = new PaymentNotifyRequest();
		pr.setAppId("money-payment");
		pr.setChannelOrderNo(String.valueOf(System.currentTimeMillis()));
		pr.setPaymentOrderNo("89");
		pr.setPaiedAmount(BigDecimal.valueOf(100L));
		return pr;
	}


}