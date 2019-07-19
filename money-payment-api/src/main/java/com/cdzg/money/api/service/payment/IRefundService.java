package com.cdzg.money.api.service.payment;

import com.cdzg.money.api.refund.request.RefundRequest;
import com.cdzg.money.api.refund.request.RefundResult;

/**
 * 支付服务接口
 * 
 * @author jiangwei
 *
 */
public interface IRefundService {

	/**
	 * 执行退款动作
	 * 
	 * @param paymentRequest
	 * @return
	 */
	public RefundResult refund(RefundRequest paymentRequest);

}
