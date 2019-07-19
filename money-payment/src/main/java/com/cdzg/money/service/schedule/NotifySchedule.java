package com.cdzg.money.service.schedule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.cdzg.money.service.notify.PaymentNotifyBiz;

@Component
public class NotifySchedule extends AbstractTask {

	@Autowired
	private PaymentNotifyBiz notifyBiz;

	@Scheduled(cron = "0/10 * * * * ?")
	public void executeFun() throws Exception {

		if (lock()) {
			try {
				notifyBiz.resue();
			} finally {
				unlock();
			}
		}

	}

}