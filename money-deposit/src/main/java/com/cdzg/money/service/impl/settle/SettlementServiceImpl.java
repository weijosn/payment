package com.cdzg.money.service.impl.settle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.dubbo.config.annotation.Service;
import com.cdzg.money.api.deposit.vo.DepositBaseResponse;
import com.cdzg.money.api.deposit.vo.settle.FundInRequest;
import com.cdzg.money.api.deposit.vo.settle.SettlementRequest;
import com.cdzg.money.exception.DepositResultCode;
import com.cdzg.money.api.service.settle.ISettlementService;
import com.cdzg.money.exception.DepositException;
import com.cdzg.money.service.module.SettlementBiz;

import lombok.extern.slf4j.Slf4j;

/**
 * 账户结算接口
 * 
 * @author jiangwei
 *
 */
@Component
@Slf4j
@Service(version = "1.0.0", timeout = 999999999, retries = -1)
public class SettlementServiceImpl implements ISettlementService {

	@Autowired
	private SettlementBiz depositSettlementBiz;

	@Override
	public DepositBaseResponse settle(SettlementRequest request) {

		if (log.isInfoEnabled()) {
			log.info("settle requestNo {} , appId {}", request.getBizNo(), request.getAppId());
		}

		DepositBaseResponse resp = new DepositBaseResponse();
		try {

			boolean result = depositSettlementBiz.settle(request);

			if (result) {
				resp.setResultCode(DepositResultCode.SUCCESS.getCode());
				resp.setResultMessage(DepositResultCode.SUCCESS.getMessage());
			} else {
				resp.setResultCode(DepositResultCode.FAIL.getCode());
				resp.setResultMessage(DepositResultCode.FAIL.getMessage());
			}

		} catch (IllegalArgumentException e) {
			log.error("requsetNo {} , illegal argument error. {}", request.getBizNo(), e.getMessage());
			resp.setResultCode(DepositResultCode.ILLEGAL_ARGUMENT.getCode());
			resp.setResultMessage(e.getMessage());
		} catch (DepositException e) {
			log.error("requsetNo {} , deposit error .{}", e.getMessage());
			resp.setResultCode(e.getErrorCode().getCode());
			resp.setResultMessage(e.getMessage());
		} catch (java.lang.Exception e) {
			log.error("requsetNo {} , settle exception.", request.getBizNo(), e);
			resp.setResultCode(DepositResultCode.FAIL.getCode());
			resp.setResultMessage(e.getMessage());
		}

		return resp;
	}

	@Override
	public DepositBaseResponse fundIn(FundInRequest request) {

		if (log.isInfoEnabled()) {
			log.info("fundIn requestNo {} , appId {} , amount {}", request.getBizNo(), request.getAppId(),request.getAmount());
		}

		DepositBaseResponse resp = new DepositBaseResponse();
		try {

			boolean result = depositSettlementBiz.findIn(request);

			if (result) {
				resp.setResultCode(DepositResultCode.SUCCESS.getCode());
				resp.setResultMessage(DepositResultCode.SUCCESS.getMessage());
			} else {
				resp.setResultCode(DepositResultCode.FAIL.getCode());
				resp.setResultMessage(DepositResultCode.FAIL.getMessage());
			}

		} catch (IllegalArgumentException e) {
			log.error("requsetNo {} , illegal argument error. {}", request.getBizNo(), e.getMessage());
			resp.setResultCode(DepositResultCode.ILLEGAL_ARGUMENT.getCode());
			resp.setResultMessage(e.getMessage());
		} catch (DepositException e) {
			log.error("requsetNo {} , deposit error .{}", e.getMessage());
			resp.setResultCode(e.getErrorCode().getCode());
			resp.setResultMessage(e.getMessage());
		} catch (java.lang.Exception e) {
			log.error("requsetNo {} , settle exception.", request.getBizNo(), e);
			resp.setResultCode(DepositResultCode.FAIL.getCode());
			resp.setResultMessage(e.getMessage());
		}

		return resp;
	}

}
