package com.cdzg.money.api.service.payment;

import com.cdzg.money.api.notify.request.NotifyResult;
import com.cdzg.money.api.notify.request.PaymentNotifyRequest;

/**
 * 非同步返回就通该接口回调
 * 
 * @author jiangwei
 *
 */
public interface IPaymentNotifyService {

	/**
	 * 执行付款通知动作
	 * 
	 * @param paymentRequest
	 * @return
	 */
	public NotifyResult notify(PaymentNotifyRequest paymentRequest);

}
