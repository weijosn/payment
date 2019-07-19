package com.cdzg.money.api.service.payment;

import com.cdzg.money.api.payment.request.PaymentRequest;
import com.cdzg.money.api.payment.request.PaymentResult;

/**
 * 支付服务接口
 *
 * @author jiangwei
 */
public interface IPaymentService {

    /**
     * 执行付款动作
     *
     * @param paymentRequest
     * @return
     */
    public PaymentResult payment(PaymentRequest paymentRequest);

    /**
     * 执行付款动作
     *
     * @param paymentRequest
     * @return
     */
    public PaymentResult refund(PaymentRequest refundRequest);
    
    /**
     * 执行入款动作
     *
     * @param paymentRequest
     * @return
     */
    public PaymentResult fundIn(PaymentRequest paymentRequest);


}
