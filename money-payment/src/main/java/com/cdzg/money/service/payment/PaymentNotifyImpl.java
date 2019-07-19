package com.cdzg.money.service.payment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cdzg.money.api.notify.request.NotifyResult;
import com.cdzg.money.api.notify.request.PaymentNotifyRequest;
import com.cdzg.money.api.service.payment.IPaymentNotifyService;
import com.cdzg.money.exception.PaymentResultCode;
import com.cdzg.money.model.PaymentContextHolder;
import com.cdzg.money.service.module.PaymentBiz;
import com.cdzg.money.service.module.SettlementBiz;

import lombok.extern.slf4j.Slf4j;

/**
 * 支付回调对外接口
 * 
 * @author jiangwei
 *
 */
@com.alibaba.dubbo.config.annotation.Service(version = "1.0.0", timeout = 999999999, retries = -1)
@Component
@Slf4j
public class PaymentNotifyImpl implements IPaymentNotifyService {

	@Autowired
	private PaymentBiz paymentBiz;

	@Autowired
	private SettlementBiz settlementBiz;

	@Override
	public NotifyResult notify(PaymentNotifyRequest request) {
		NotifyResult pr = new NotifyResult();
		try {
			PaymentContextHolder.getInstance();
			if (paymentBiz.payCallback(request)) {
				pr.setResultCode(PaymentResultCode.SUCCESS.getCode());
				pr.setResultMessage(PaymentResultCode.SUCCESS.getMessage());
				// 执行下一结算阶段
				this.settlementBiz.settle();
				this.settlementBiz.clearing();
			} else {
				pr.setResultCode(PaymentResultCode.ACCOUNTING_STAGE_ERROR.getCode());
				pr.setResultMessage(PaymentResultCode.ACCOUNTING_STAGE_ERROR.getMessage());
			}
		} catch (Exception e) {
			log.error("do notify error ", e);
		} finally {
			PaymentContextHolder.clearPaymentContext();
		}
		return pr;
	}

}
