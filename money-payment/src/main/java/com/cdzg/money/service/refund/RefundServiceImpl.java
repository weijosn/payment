package com.cdzg.money.service.refund;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cdzg.money.api.refund.request.RefundRequest;
import com.cdzg.money.api.refund.request.RefundResult;
import com.cdzg.money.api.service.payment.IRefundService;
import com.cdzg.money.exception.PaymentException;
import com.cdzg.money.exception.PaymentResultCode;
import com.cdzg.money.model.PaymentContextHolder;
import com.cdzg.money.service.module.RefundBiz;
import com.cdzg.money.service.module.RequestValidatorBiz;

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
public class RefundServiceImpl implements IRefundService {

	@Autowired
	private RefundBiz refundBiz;
	@Autowired
	private RequestValidatorBiz refundRequestValidator;

	@Override
	public RefundResult refund(RefundRequest refundRequest) {

		log.info("apply refund , body {}", refundRequest);

		RefundResult result = new RefundResult();

		try {

			refundRequestValidator.validate(refundRequest);

			refundBiz.saveRefundInstruction(refundRequest);

			refundBiz.refund(refundRequest);

			result = refundBiz.query(refundRequest.getPaymentOrderId());

		} catch (java.lang.IllegalArgumentException e) {
			log.warn("apply refund illegal argument error , {}", e.getMessage());
			result.setResultCode(PaymentResultCode.ILLEGAL_ARGUMENT.getCode());
			result.setResultMessage(e.getMessage());
		} catch (PaymentException e) {
			log.error("apply refund error. ", e);
			result.setResultCode(e.getErrorCode().getCode());
			result.setResultMessage(e.getErrorCode().getMessage());
		} catch (Exception e) {
			result.setResultCode(PaymentResultCode.FAIL.getCode());
			result.setResultMessage(PaymentResultCode.FAIL.getMessage());
		} finally {
			PaymentContextHolder.clearPaymentContext();
		}

		log.info("apply refund response {}", result);

		return result;
	}

}
