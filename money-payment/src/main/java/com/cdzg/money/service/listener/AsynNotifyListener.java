package com.cdzg.money.service.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.cdzg.money.mapper.PaymentOrderMapper;
import com.cdzg.money.mapper.RefundOrderMapper;
import com.cdzg.money.mapper.entity.PaymentOrderPO;
import com.cdzg.money.mapper.entity.RefundOrderPO;
import com.cdzg.money.model.enums.OrderStatusEnum;
import com.cdzg.money.model.enums.PaymentCodeEnum;
import com.cdzg.money.service.notify.PaymentNotifyBiz;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AsynNotifyListener implements ApplicationListener<AsynNotifyEvent> {

	@Autowired
	private PaymentOrderMapper paymentMapper;
	@Autowired
	private RefundOrderMapper refundOrderMapper;
	@Autowired
	private PaymentNotifyBiz paymentNotifyBiz;

	@Async
	@Override
	public void onApplicationEvent(AsynNotifyEvent event) {

		log.info("asyn notify listener receiving message {}", event);

		if (event.getSource() == null) {
			return;
		}

		final String paymentOrderId = event.getSource().toString();

		//判断是否退款订单
		refundHandle(paymentOrderId);
		//进行通知
		notifyHandle(paymentOrderId);
	}

	private void notifyHandle(String paymentOrderId) {
		paymentNotifyBiz.doNotify(paymentOrderId);
	}

	private void refundHandle(String paymentOrderId) {
		final PaymentOrderPO order = paymentMapper.findByOrderId(paymentOrderId);
		final OrderStatusEnum orderStatus = OrderStatusEnum.getByCode(order.getOrderStatus());
		if (orderStatus != OrderStatusEnum.SUCCESS) {
			return;
		}
		if (PaymentCodeEnum.getByCode(order.getPaymentCode()) != PaymentCodeEnum.REFUND) {
			return;
		}
		// 更新退款订单信息
		RefundOrderPO refundOrderPO = refundOrderMapper.findByPaymentOrderId(paymentOrderId);
		if (refundOrderPO != null) {
			refundOrderMapper.updateToSucced(refundOrderPO.getId(), order.getPaiedAmount());
		}
	}
}
