package com.cdzg.money.service.module;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.Assert;

import com.cdzg.money.api.notify.request.PaymentNotifyRequest;
import com.cdzg.money.exception.ExceptionUtils;
import com.cdzg.money.exception.PaymentException;
import com.cdzg.money.exception.PaymentResultCode;
import com.cdzg.money.mapper.NotifyMapper;
import com.cdzg.money.mapper.PaymentOrderMapper;
import com.cdzg.money.mapper.PaymentRequestMapper;
import com.cdzg.money.mapper.PaymentStageMapper;
import com.cdzg.money.mapper.entity.NotifyPO;
import com.cdzg.money.mapper.entity.PaymentOrderPO;
import com.cdzg.money.mapper.entity.PaymentRequestPO;
import com.cdzg.money.mapper.entity.PaymentStagePO;
import com.cdzg.money.model.PaymentContext;
import com.cdzg.money.model.PaymentContextHolder;
import com.cdzg.money.model.enums.NextvalTypeEnum;
import com.cdzg.money.model.enums.OrderStatusEnum;
import com.cdzg.money.model.enums.OrderTypeEnum;
import com.cdzg.money.model.enums.PaymentCodeEnum;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class PaymentBiz {

	@Autowired
	private PaymentStageMapper paymentStageMapper;
	@Autowired
	private NotifyMapper notifyMapper;
	@Autowired
	private PaymentOrderMapper paymentMapper;
	@Autowired
	private TransactionTemplate transactionTemplate;
	@Autowired
	private NextvalBiz nextvalBiz;
	@Autowired
	private PaymentRequestMapper paymentRequestMapper;

	// 支付回调
	public boolean payCallback(PaymentNotifyRequest request) {

		Long id = Long.parseLong(request.getPaymentOrderNo());

		PaymentStagePO stage = this.paymentStageMapper.findById(id);
		Assert.isTrue(stage != null, "payment stage not found , stage id " + request.getPaymentOrderNo());

		PaymentContext paymentContext = PaymentContextHolder.getInstance();
		paymentContext.setPaymentOrderId(stage.getPaymentOrderId());
		
		
		if(OrderStatusEnum.getByCode(stage.getOrderStatus()) == OrderStatusEnum.SUCCESS ) {
			return true;
		}
		
		if (request.getPaiedAmount()!=null  && request.getPaiedAmount().compareTo(stage.getOrderAmount()) >= 0) {
			if (1 == paymentStageMapper.updateToSucced(id, request.getPaiedAmount(), new Date(),OrderStatusEnum.PENDING.getCode())) {
				return true;
			}
		} else {
			log.error("callback amount error.{}", request.toString());
		}

		return false;
	}
	
	// 存单
	public void savePaymentInstruction() {

		// 落主订单
		final String orderId = nextvalBiz.nextval(NextvalTypeEnum.ORDER);
		final PaymentContext context = PaymentContextHolder.getInstance();
		context.setPaymentOrderId(orderId);

		final PaymentOrderPO paymentOrder = buildPaymentOrder();
		final NotifyPO notify = buildNotify();
		final PaymentRequestPO paymentRequest = buildPaymentRequest();
		final PaymentCodeEnum paymentCode = PaymentCodeEnum.getByCode(paymentOrder.getPaymentCode());
		// 落地分单
		final List<PaymentStagePO> stages = buildPaymentStages(paymentOrder.getPaymentOrderId());

		try {
			transactionTemplate.execute(new TransactionCallbackWithoutResult() {
				@Override
				protected void doInTransactionWithoutResult(TransactionStatus status) {
					if(paymentCode == PaymentCodeEnum.REFUND) {
						if( paymentMapper.closeOrder(context.getOrgPaymentOrderId()) !=1 ) {
							throw new PaymentException(PaymentResultCode.CLOSE_PAYMENT_ERROR,PaymentResultCode.CLOSE_PAYMENT_ERROR.getMessage());
						}
					}
					// 存请求
					paymentRequestMapper.save(paymentRequest);
					// 先存订单
					paymentMapper.save(paymentOrder);
					// 支付分段
					paymentStageMapper.batchSave(stages);
					// 存回调信息
					if(StringUtils.isNotEmpty(notify.getNotifyUrl())) {
						notifyMapper.save(notify);
					}
				}
			});
		} catch (DataAccessException e) {
			// 判断是否为主健冲突
			if (ExceptionUtils.isDuplicate(e)) {
				PaymentRequestPO p = paymentRequestMapper.findByBizNo(context.getTradeNo(), context.getAppId());
				PaymentOrderPO order = paymentMapper.findByOrderId(p.getPaymentOrderId());
				if (PaymentCodeEnum.getByCode(p.getPaymentCode()) != context.getPaymentCode()) {
					throw new PaymentException(PaymentResultCode.DUPLICATE_BIZNO,PaymentResultCode.DUPLICATE_BIZNO.getMessage());
				}
				// 已失败的订单不可以发起
				if (OrderStatusEnum.getByCode(order.getOrderStatus()) == OrderStatusEnum.REFUND) {
					throw new PaymentException(PaymentResultCode.PAYMENT_STATUS_ERROR,PaymentResultCode.PAYMENT_STATUS_ERROR.getMessage());
				}
				PaymentContextHolder.getInstance().setPaymentOrderId(p.getPaymentOrderId());
				return;
			}
			throw e;
		}

	}

	public PaymentOrderPO findByOrderNo(String orderNo) {
		return this.paymentMapper.findByOrderId(orderNo);
	}

	private PaymentRequestPO buildPaymentRequest() {
		PaymentContext pc = PaymentContextHolder.getInstance();
		PaymentRequestPO request = new PaymentRequestPO();
		request.setBizNo(pc.getTradeNo());
		request.setCreateTime(new Date());
		request.setPaymentOrderId(pc.getPaymentOrderId());
		request.setPaymentCode(pc.getPaymentCode().getCode());
		request.setPaymentVersion(StringUtils.EMPTY);
		request.setRemark(StringUtils.left(pc.getRemark(), 64));
		request.setAppId(pc.getAppId());
		return request;
	}

	private List<PaymentStagePO> buildPaymentStages(final String orderId) {
		PaymentContext pc = PaymentContextHolder.getInstance();
		List<PaymentStagePO> list = new ArrayList<PaymentStagePO>();
		pc.getPaymentInstruction().getStages().forEach(value -> {
			PaymentStagePO po = new PaymentStagePO();
			po.setFeeAmount(BigDecimal.ZERO);
			po.setOrderAmount(value.getAmount());
			po.setPaymentOrderId(orderId);
			po.setPaiedAmount(BigDecimal.ZERO);
			po.setPayPlat(StringUtils.EMPTY);
			po.setPayType(StringUtils.EMPTY);
			po.setReadlyTime(new Date());
			po.setPaymentTime(new Date());
			po.setCreateTime(new Date());
			po.setSummary(StringUtils.EMPTY);
			po.setOrderStatus(OrderStatusEnum.INIT.getCode());
			po.setPayeeAccountId(value.getPayee().getAccountNo());
			po.setPayerAccountId(value.getPayer().getAccountNo());
			list.add(po);
		});
		return list;
	}

	// 支付订单生成
	private PaymentOrderPO buildPaymentOrder() {
		PaymentContext pc = PaymentContextHolder.getInstance();

		PaymentOrderPO order = new PaymentOrderPO();
		order.setCreateTime(new Date());
		order.setGoodsName(pc.getGoodsName());
		order.setGoodsPrice(pc.getGoodsPrice());

		order.setAppId(pc.getAppId());
		order.setOrderAmount(pc.getPaymentInstruction().getTotalAmount());
		order.setPaymentOrderId(pc.getPaymentOrderId());

		order.setOrderStatus(OrderStatusEnum.PENDING.getCode());
		order.setOrderType(OrderTypeEnum.NORMAL.getCode());
		order.setPaiedAmount(BigDecimal.ZERO);
		order.setPaymentCode(pc.getPaymentCode().getCode());

		order.setRemark(pc.getRemark());
		order.setTradeNo(pc.getTradeNo());

		// 支付时间
		order.setPaymentTime(Date.from(LocalDate.of(1990, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
		order.setUpdateTime(new Date());
		order.setReadlyTime(new Date());
		order.setCreateTime(new Date());

		return order;
	}
	
	private NotifyPO  buildNotify() {
		PaymentContext pc = PaymentContextHolder.getInstance();
		NotifyPO po = new NotifyPO();
		po.setCreateTime(new Date());
		po.setGoodsName(StringUtils.EMPTY);
		po.setNotifyStatus(NotifyPO.NOTIFY_WAIT);
		po.setNotifyTime(new Date());
		po.setNotifyTimes(0);
		po.setNotifyUrl(pc.getPaymentInstruction().getNotifyURL());
		po.setPaymentOrderNo(pc.getPaymentOrderId());
		po.setResponseCode(-1);
		return po;
	}


}
