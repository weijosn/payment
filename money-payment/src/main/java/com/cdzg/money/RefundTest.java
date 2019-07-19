package com.cdzg.money;

import java.math.BigDecimal;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.alibaba.dubbo.config.annotation.Reference;
import com.cdzg.money.api.refund.request.RefundRequest;
import com.cdzg.money.api.refund.request.RefundResult;
import com.cdzg.money.api.service.payment.IRefundService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RefundTest {

	@Reference(version = "1.0.0", check = false, timeout = 999999999, retries = -1)
	private IRefundService refundService;

	@Test
	public void trade() throws Exception {
		RefundResult rep = this.refundService.refund(buildPaymentRequest());
		System.out.println(rep);
	}

	private RefundRequest buildPaymentRequest() {
		String bizNo = "";
		bizNo = String.valueOf(System.currentTimeMillis());
		RefundRequest pr = new RefundRequest();
		pr.setAppId("money-payment");
		pr.setTradeNo(bizNo);
		pr.setCallbackAddress("http://www.baidu.com");
		pr.setRefundAmount(BigDecimal.valueOf(1));
		pr.setPaymentOrderId("201904101000000008888");
		
		return pr;
	}


}