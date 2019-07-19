package com.cdzg.money;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import org.assertj.core.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.alibaba.dubbo.config.annotation.Reference;
import com.cdzg.money.api.deposit.vo.DepositBaseResponse;
import com.cdzg.money.api.deposit.vo.settle.FundInRequest;
import com.cdzg.money.api.deposit.vo.settle.TargetAccount;
import com.cdzg.money.api.service.account.IAccountService;
import com.cdzg.money.api.service.settle.ISettlementService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FundInTest {

	@Reference(version = "1.0.0", check = false, timeout = 999999999, retries = -1)
	private IAccountService accountService;
	@Reference(version = "1.0.0", check = false, timeout = 999999999, retries = -1)
	private ISettlementService settlementService;

	@Test
	public void fundIn() throws Exception {
		ArrayList<Object> list = Lists.newArrayList();
		int start = 20000;
		for (int i = start; i < start + 100; i++) {
			list.add(i);
		}
		AtomicInteger total = new AtomicInteger();
		String token = String.valueOf(System.currentTimeMillis());
		list.parallelStream().forEach(value -> {
			FundInRequest request = new FundInRequest();
			request.setAppId("111");
			request.setBizNo(String.valueOf(value));
			request.setAccount(TargetAccount.instance("31030016358687917375750295", "充值"));
			request.setAmount(BigDecimal.valueOf(10));
			request.setSuiteNo(token);
			DepositBaseResponse rep = settlementService.fundIn(request);
			System.out.println(rep.getResultCode()+"---"+rep.getResultMessage());
			total.addAndGet(10);
		});
		Thread.sleep(128 * 1000);
		System.out.println("增加的金额:" + total);
	}

}