package com.cdzg.money.service.payment;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cdzg.money.api.payment.request.PartyInfo;
import com.cdzg.money.api.payment.request.PaymentRequest;
import com.cdzg.money.api.payment.request.PaymentResult;
import com.cdzg.money.api.service.payment.IPaymentService;
import com.cdzg.money.exception.PaymentException;
import com.cdzg.money.exception.PaymentResultCode;
import com.cdzg.money.exception.SuspendSettleException;
import com.cdzg.money.mapper.entity.PaymentOrderPO;
import com.cdzg.money.model.PaymentContext;
import com.cdzg.money.model.PaymentContextHolder;
import com.cdzg.money.model.PaymentInstruction;
import com.cdzg.money.model.StageInstruction;
import com.cdzg.money.model.enums.PaymentCodeEnum;
import com.cdzg.money.service.module.PaymentBiz;
import com.cdzg.money.service.module.RequestValidatorBiz;
import com.cdzg.money.service.module.SettlementBiz;

import lombok.extern.slf4j.Slf4j;

/**
 * 支付业务对外接口
 * 
 * @author jiangwei
 *
 */
@com.alibaba.dubbo.config.annotation.Service(version = "1.0.0", timeout = 999999999, retries = -1)
@Component
@Slf4j
public class PaymentServiceImpl implements IPaymentService {

	@Autowired
	private PaymentBiz paymentBiz;
	@Autowired
	private SettlementBiz settlementBiz;
	@Autowired
	private RequestValidatorBiz paymentRequestValidator;

	/**
	 * 执行支付<br>
	 * 支付的业务边界为自控制支付，不负责业务数据的管理及提供。
	 * 
	 * @param instruction
	 * @return
	 */
	public PaymentResult doPayment(PaymentRequest request, PaymentCodeEnum productCode) {

		try {
			// 初始化支付上下文
			buildInstruction(request,productCode);

			// 1. 初始支付指令
			paymentBiz.savePaymentInstruction();

			// 2、清分，无配置协议
			// paymentBiz.clearingApply();

			// 3. 执行结算
			settlementBiz.settle();
			
			// 4. 更新结算
			settlementBiz.clearing();
			
			// 5. 构造支付结果
			return buildPaymentResult();

		} catch (IllegalArgumentException e) {
			log.info("支付请求处理失败 {}:{}", e.getMessage());
			return buildPaymentExceptionResult(e);
		} catch (SuspendSettleException e) {
			return buildPaymentResult();
		}catch (PaymentException e) {
			log.info("支付请求处理失败 {}", e.getMessage());
			return buildPaymentExceptionResult(e);
		} catch (Exception e) {
			log.error("支付请求处理异常", e);
			return buildPaymentExceptionResult(new PaymentException(PaymentResultCode.FAIL, PaymentResultCode.FAIL.getMessage()));
		} finally {
			PaymentContextHolder.clearPaymentContext();
		}
	}

	public PaymentResult payment(PaymentRequest paymentRequest) {
		return this.doPayment(paymentRequest,PaymentCodeEnum.PAYMENT);
	}

	public PaymentResult fundIn(PaymentRequest paymentRequest) {
		return this.doPayment(paymentRequest,PaymentCodeEnum.FUND_IN);
	}

	public PaymentResult refund(PaymentRequest paymentRequest) {
		return this.doPayment(paymentRequest,PaymentCodeEnum.REFUND);
	}

	private PaymentResult buildPaymentExceptionResult(IllegalArgumentException e) {
		PaymentResult pr = new PaymentResult();
		pr.setResultCode(PaymentResultCode.ILLEGAL_ARGUMENT.getCode());
		pr.setResultMessage(e.getMessage());
		return pr;
	}

	private PaymentResult buildPaymentExceptionResult(PaymentException e) {
		PaymentResult pr = new PaymentResult();
		pr.setResultCode(e.getErrorCode().getCode());
		pr.setResultMessage(e.getMessage());
		return pr;
	}

	private PaymentResult buildPaymentResult() {
		PaymentContext paymentContext = PaymentContextHolder.getInstance();
		PaymentOrderPO paymentOrder = this.paymentBiz.findByOrderNo(paymentContext.getPaymentOrderId());
		PaymentResult pr = new PaymentResult();
		pr.setPaiedAmount(paymentOrder.getPaiedAmount());
		pr.setOrderAmount(paymentOrder.getOrderAmount());
		pr.setOrderStatus(paymentOrder.getOrderStatus());
		pr.setOrderId(paymentOrder.getPaymentOrderId());
		
		pr.setResultCode(PaymentResultCode.SUCCESS.getCode());
		pr.setResultMessage(PaymentResultCode.SUCCESS.getMessage());
		
		return pr;
	}

	private PaymentInstruction buildInstruction(PaymentRequest request,PaymentCodeEnum paymentCodeEnum) {

		paymentRequestValidator.validate(request,paymentCodeEnum);
		
		PaymentContext paymentContext = PaymentContextHolder.getInstance();

		PaymentInstruction pi = new PaymentInstruction();

		// 分段
		pi.setStages(buildStages(request));

		pi.setMemberId(request.getPaymentPartyInfo().getBizInitiator().getMemberId());
		// 总额
		pi.setTotalAmount(request.getPaymentOrderInfo().getPayAmount());

		// 地址
		pi.setReturnURL(StringUtils.EMPTY);
		pi.setNotifyURL(request.getPaymentOrderInfo().getNotifyurl());

		// 检查一下数据是否正确
		pi.validate();

		paymentContext.setPaymentInstruction(pi);
		paymentContext.setAppId(request.getAppId());
		paymentContext.setGoodsName(request.getPaymentOrderInfo().getGoodsName());
		paymentContext.setGoodsPrice(request.getPaymentOrderInfo().getPayAmount());
		paymentContext.setRemark(request.getPaymentOrderInfo().getPayRemark());
		paymentContext.setOrgPaymentOrderId(request.getPaymentOrderInfo().getOrgPaymentOrderId());
		paymentContext.setTradeNo(request.getTradeNo());
		paymentContext.setPaymentCode(paymentCodeEnum);

		return pi;
	}

	/**
	 * 构建分段指令
	 * 
	 * @param request
	 * @return List<StageInstruction>
	 */
	private List<StageInstruction> buildStages(PaymentRequest request) {
		
		final PartyInfo payee = request.getPaymentPartyInfo().getPayee();
		final PartyInfo payer = request.getPaymentPartyInfo().getPayer();

		List<StageInstruction> stages = new ArrayList<StageInstruction>();
		stages.add(StageInstruction.newState(request.getPaymentOrderInfo().getPayAmount(), payer, payee));
		
		return stages;
	}

}
