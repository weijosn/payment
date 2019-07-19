package com.cdzg.money.service.module;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import com.alibaba.dubbo.config.annotation.Reference;
import com.cdzg.money.api.deposit.vo.DepositBaseResponse;
import com.cdzg.money.api.deposit.vo.settle.SettlementRequest;
import com.cdzg.money.api.payment.request.PartyInfo;
import com.cdzg.money.api.payment.request.PaymentOrderInfo;
import com.cdzg.money.api.payment.request.PaymentPartyInfo;
import com.cdzg.money.api.payment.request.PaymentRequest;
import com.cdzg.money.api.payment.request.PaymentResult;
import com.cdzg.money.api.refund.request.RefundRequest;
import com.cdzg.money.api.refund.request.RefundResult;
import com.cdzg.money.api.service.payment.IPaymentService;
import com.cdzg.money.api.service.settle.ISettlementService;
import com.cdzg.money.exception.ExceptionUtils;
import com.cdzg.money.exception.PaymentException;
import com.cdzg.money.exception.PaymentResultCode;
import com.cdzg.money.exception.SuspendSettleException;
import com.cdzg.money.mapper.PaymentOrderMapper;
import com.cdzg.money.mapper.PaymentStageMapper;
import com.cdzg.money.mapper.RefundOrderMapper;
import com.cdzg.money.mapper.entity.PaymentOrderPO;
import com.cdzg.money.mapper.entity.PaymentStagePO;
import com.cdzg.money.mapper.entity.RefundOrderPO;
import com.cdzg.money.model.enums.NextvalTypeEnum;
import com.cdzg.money.model.enums.OrderStatusEnum;
import com.cdzg.money.model.enums.RefundStatusEnum;
import com.cdzg.money.service.module.deposit.DepositSettleProtocol;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class RefundBiz {

	@Reference(version = "1.0.0", check = false, timeout = 999999999, retries = -1)
	private ISettlementService settlementService;
	@Autowired
	private IPaymentService iPaymentService;
	@Autowired
	private TransactionTemplate transactionTemplate;
	@Autowired
	private NextvalBiz nextvalBiz;
	@Autowired
	private RefundOrderMapper refundOrderMapper;
	@Autowired
	private PaymentOrderMapper paymentOrderMapper;
	@Autowired
	private PaymentStageMapper paymentStageMapper;
    @Autowired
    private DepositSettleProtocol depositSettleProtocol;
    
	public RefundResult query(String paymentOrderId) {
		RefundResult result = new RefundResult();
		RefundOrderPO refundOrder = refundOrderMapper.findByPaymentOrderId(paymentOrderId);
		result.setOrderAmount(refundOrder.getRefundAmount());
		result.setOrderId(refundOrder.getPaymentOrderId());
		result.setOrderStatus(refundOrder.getRefundStatus());
		result.setRefundAmount(refundOrder.getActualRefundAmount());
		result.setResultCode(PaymentResultCode.SUCCESS.getCode());
		return result;
	}

	public void refund(RefundRequest refundRequest) throws SuspendSettleException, PaymentException {

		final String paymentOrderId = refundRequest.getPaymentOrderId();
		final PaymentOrderPO paymentOrder = paymentOrderMapper.findByOrderId(paymentOrderId);
		final RefundOrderPO refundOrderPO = refundOrderMapper.findByPaymentOrderId(paymentOrderId);
		final RefundStatusEnum refundStatus = RefundStatusEnum.getByCode(refundOrderPO.getRefundStatus());

		// 已完成退款
		if (refundStatus == RefundStatusEnum.SUCCESS) {
			return;
		}
		
		// 解冻没完成的订单
		List<PaymentStagePO> stages = this.paymentStageMapper.listByOrderId(paymentOrderId);
		List<PaymentStagePO>  succedStages = stages.stream().filter(value->OrderStatusEnum.getByCode(value.getOrderStatus()) == OrderStatusEnum.SUCCESS).collect(Collectors.toList());
		List<PaymentStagePO>  pendingStages = stages.stream().filter(value->OrderStatusEnum.getByCode(value.getOrderStatus()) == OrderStatusEnum.PENDING).collect(Collectors.toList());
		// 非明确的结果
		if (pendingStages.size() > 0) {
			throw new PaymentException(PaymentResultCode.PAYMENT_STAGE_STATUS_ERROR, PaymentResultCode.PAYMENT_STAGE_STATUS_ERROR.getMessage());
		}
		
		// 已在处理当中
		if (refundOrderMapper.updateToPending(refundOrderPO.getId()) != 1) {
			return;
		}
	
		// 存在渠道成功的记录则冲退
		if(succedStages.size() > 0 ) {
			PaymentRequest peRefundRequest = buildRefundPeRequest(paymentOrder.getPaymentOrderId());
			peRefundRequest.getPaymentOrderInfo().setNotifyurl(refundRequest.getCallbackAddress());
			peRefundRequest.getPaymentOrderInfo().setPayRemark(refundRequest.getRemark());
			clearing(paymentOrderId,iPaymentService.refund(peRefundRequest));
		} else {
			// 外部渠道无成功记录,直接退款
			clearing(paymentOrderId,unfreezn(refundRequest.getPaymentOrderId(),refundRequest.getTradeNo()));
		}
		
	}

	private void clearing(String paymentOrderId,PaymentResult paymentResult) {
		final RefundOrderPO refundOrderPO = refundOrderMapper.findByPaymentOrderId(paymentOrderId);
		if(paymentResult!=null) {
			switch (PaymentResultCode.getByCode(paymentResult.getResultCode())) {
			case SUCCESS:
				if (paymentResult.getPaiedAmount().compareTo(refundOrderPO.getRefundAmount()) >= 0) {
					refundOrderMapper.updateToSucced(refundOrderPO.getId(), paymentResult.getPaiedAmount());
				}
				break;
			case SUSPEND:
				break;
			default:
				refundOrderMapper.updateToFailed(refundOrderPO.getId());
			}		
		}
	}
	
	private void clearing(String paymentOrderId, DepositBaseResponse unfreeznResult) {
		final RefundOrderPO refundOrderPO = refundOrderMapper.findByPaymentOrderId(paymentOrderId);
		if (unfreeznResult.isSuccess()) {
			refundOrderMapper.updateToSucced(refundOrderPO.getId(), refundOrderPO.getRefundAmount());
		} else {
			refundOrderMapper.updateToFailed(refundOrderPO.getId());
		}
	}

	// 解冻
	private DepositBaseResponse unfreezn(String paymentOrderId,String bizNo) {
		List<PaymentStagePO> stages = paymentStageMapper.listByOrderId(paymentOrderId)
				.stream().filter(value->OrderStatusEnum.getByCode(value.getOrderStatus()) != OrderStatusEnum.SUCCESS).collect(Collectors.toList());
		if(stages.size() == 0) {
			return null ;
		}
		SettlementRequest request = new SettlementRequest();
		request.setAppId("money-payment");
		request.setBizNo(bizNo);
		request.setUnfreezeAccountings(depositSettleProtocol.buildUnfreeznInstruction(stages));
		DepositBaseResponse freeznResponse = this.settlementService.settle(request);
		if (!freeznResponse.isSuccess()) {
			log.error("payment instruction runtime error.unfreezn accounting failed.orderId {}", paymentOrderId);
			throw new PaymentException(PaymentResultCode.ACCOUNTING_SETTLEMENT_ERROR, PaymentResultCode.ACCOUNTING_SETTLEMENT_ERROR.getMessage());
		}
		return freeznResponse;
	}

	public void saveRefundInstruction(RefundRequest refundRequest) {
		// 落主订单
		final RefundOrderPO refundOrder = buildRefundOrder(refundRequest);

		try {
			transactionTemplate.execute(new TransactionCallbackWithoutResult() {
				@Override
				protected void doInTransactionWithoutResult(TransactionStatus status) {
					// 存订单
					refundOrderMapper.save(refundOrder);
				}
			});
		} catch (DataAccessException e) {
			// 判断是否为主健冲突
			if (ExceptionUtils.isDuplicate(e)) {
				return;
			}
			throw e;
		}
	}

	private RefundOrderPO buildRefundOrder(RefundRequest refundRequest) {
		Date defaultDate = Date.from(LocalDate.of(1990, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant());
		RefundOrderPO po = new RefundOrderPO();
		po.setRefundOrderId(nextvalBiz.nextval(NextvalTypeEnum.REFUND));
		po.setActualRefundAmount(BigDecimal.ZERO);
		po.setCreateTime(new Date());
		po.setPaymentOrderId(refundRequest.getPaymentOrderId());
		po.setRealyTime(defaultDate);
		po.setRefundAmount(refundRequest.getRefundAmount());
		po.setRefundStatus(RefundStatusEnum.INIT.getCode());
		po.setRefundTime(defaultDate);
		po.setAppId(refundRequest.getAppId());
		po.setTradeNo(refundRequest.getTradeNo());
		return po;
	}

	private PaymentRequest buildRefundPeRequest(String paymentOrderId) {
		final RefundOrderPO refundOrder = refundOrderMapper.findByPaymentOrderId(paymentOrderId);
		final List<PaymentStagePO> stages = paymentStageMapper.listByOrderId(paymentOrderId);
		final PaymentRequest paymentRequest = new PaymentRequest();
		final PaymentOrderInfo paymentOrderInfo = new PaymentOrderInfo();
		final PaymentOrderPO paymentOrder = paymentOrderMapper.findByOrderId(paymentOrderId);
		final PaymentPartyInfo paymentPartyInfo = new PaymentPartyInfo(); {
			paymentOrderInfo.setGoodsName(paymentOrder.getGoodsName());
			paymentOrderInfo.setPayAmount(refundOrder.getRefundAmount());
			paymentOrderInfo.setOrgPaymentOrderId(paymentOrderId);
		}
		paymentRequest.setPaymentOrderInfo(paymentOrderInfo);
		paymentRequest.setPaymentPartyInfo(paymentPartyInfo);
		paymentRequest.setTradeNo(refundOrder.getTradeNo());
		paymentRequest.setAppId(refundOrder.getAppId());
		// 可退款数据
		stages.forEach(stage -> {
			if (OrderStatusEnum.refundable(stage.getOrderStatus())) {
				paymentPartyInfo.setPayee(PartyInfo.instance(stage.getPayerAccountId(), StringUtils.EMPTY));
				paymentPartyInfo.setPayer(PartyInfo.instance(stage.getPayeeAccountId(), StringUtils.EMPTY));
				paymentPartyInfo.setBizInitiator(paymentPartyInfo.getPayer());
			}
		});
		return paymentRequest;
	}

}
