package com.cdzg.money.service.module.deposit;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Component;

import com.alibaba.dubbo.config.annotation.Reference;
import com.cdzg.money.api.deposit.vo.DepositBaseResponse;
import com.cdzg.money.api.deposit.vo.settle.DebitCreditAccounting;
import com.cdzg.money.api.deposit.vo.settle.FreezeAccounting;
import com.cdzg.money.api.deposit.vo.settle.SettlementRequest;
import com.cdzg.money.api.deposit.vo.settle.TargetAccount;
import com.cdzg.money.api.deposit.vo.settle.UnfreezeAccounting;
import com.cdzg.money.api.service.settle.ISettlementService;
import com.cdzg.money.exception.PaymentException;
import com.cdzg.money.exception.PaymentResultCode;
import com.cdzg.money.mapper.entity.PaymentStagePO;
import com.cdzg.money.model.PaymentContext;
import com.cdzg.money.model.PaymentContextHolder;
import com.cdzg.money.service.module.SettlementChinaBiz.SettlementChinaInstruction;

import lombok.extern.slf4j.Slf4j;

/**
 * 账户结算
 * @author jiangwei
 *
 */
@Component
@Slf4j
public class DepositSettleProtocol {

	@Reference(version = "1.0.0", check = false, timeout = 999999999, retries = -1)
	private ISettlementService settlementService;
	
	// 执行内部结算动作
	public void settle(SettlementChinaInstruction settlementChinaInstruction) {
		PaymentContext paymentContext = PaymentContextHolder.getInstance();
		DepositBaseResponse settleResponse = settlementService.settle(_buildSettleInstruction(settlementChinaInstruction.getStages(),settlementChinaInstruction.hasFreezn()));
		if (!settleResponse.isSuccess()) {
			log.warn("payment instruction runtime error.settle accounting failed.orderId {}",paymentContext.getPaymentOrderId());
			throw new PaymentException(PaymentResultCode.ACCOUNTING_SETTLEMENT_ERROR,PaymentResultCode.ACCOUNTING_SETTLEMENT_ERROR.getMessage());
		}
	}
	
	// 执行冻结动作
	public void freezn(SettlementChinaInstruction settlementChinaInstruction) {
		DepositBaseResponse freeznResponse = this.settlementService.settle(_buildFreeznInstruction(settlementChinaInstruction.getStages()));
		if (!freeznResponse.isSuccess()) {
			log.error("payment instruction runtime error.freezn accounting failed.orderId {}", settlementChinaInstruction.getPaymentContext().getPaymentOrderId());
			throw new PaymentException(PaymentResultCode.ACCOUNTING_SETTLEMENT_ERROR, PaymentResultCode.ACCOUNTING_SETTLEMENT_ERROR.getMessage());
		}
	}

	private SettlementRequest _buildFreeznInstruction(List<PaymentStagePO> stages) {
		PaymentContext paymentContext = PaymentContextHolder.getInstance();
		String bizNo = paymentContext.getPaymentOrderId().concat("F");
		SettlementRequest request = new SettlementRequest();
		request.setAppId("money-payment");
		request.setBizNo(bizNo);
		List<FreezeAccounting> accountings = new ArrayList<FreezeAccounting>();
		stages.forEach(stage -> {
			FreezeAccounting ac = new FreezeAccounting();
			ac.setAmount(stage.getOrderAmount());
			ac.setCurrencyCode("CNY");
			ac.setOrigBizNo(stage.buildFreeznId());
			ac.setSettlementTime(new Date());
			ac.setSummary("出款冻结");
			ac.setTargetAccount(TargetAccount.instance(stage.getPayerAccountId(), ""));
			accountings.add(ac);
		});
		request.setFreezeAccountings(accountings);
		return request;
	}

	// 内部结算指令
	private SettlementRequest _buildSettleInstruction(List<PaymentStagePO> stages, boolean unfreezn) {
		PaymentContext paymentContext = PaymentContextHolder.getInstance();
		String bizNo = paymentContext.getPaymentOrderId().concat("S");
		SettlementRequest request = new SettlementRequest();
		request.setAppId("money-payment");
		request.setBizNo(bizNo);
		request.setDebitCreditAccountings(_buildAccountingInstruction(stages));
		// 如果是交易则需要解冻
		if (unfreezn) {
			request.setUnfreezeAccountings(buildUnfreeznInstruction(stages));
		}
		// 判断是否为退款，退款需要解冻原始订单
//		if (paymentContext.getPaymentCode() == PaymentCodeEnum.REFUND) {
//			List<PaymentStagePO> orgStages = this.paymentStageMapper.listByOrderId(paymentContext.getOrgPaymentOrderId());
//			request.getUnfreezeAccountings().addAll(buildUnfreeznInstruction(orgStages));
//		}
		return request;
	}

	private List<DebitCreditAccounting> _buildAccountingInstruction(List<PaymentStagePO> stages) {
		List<DebitCreditAccounting> accountings = new ArrayList<DebitCreditAccounting>();
		stages.stream().forEach(stage -> {
			DebitCreditAccounting accounting = new DebitCreditAccounting();
			accounting.setAmount(stage.getOrderAmount());
			accounting.setCurrencyCode("CNY");
			{
				TargetAccount creditAccount = new TargetAccount();
				creditAccount.setAccountId(stage.getPayeeAccountId());
				creditAccount.setSummary("收款");
				accounting.setCreditAccount(creditAccount);
			}
			{
				TargetAccount debitAccount = new TargetAccount();
				debitAccount.setAccountId(stage.getPayerAccountId());
				debitAccount.setSummary("付款");
				accounting.setDebitAccount(debitAccount);
			}
			accounting.setSettlementTime(new Date());
			accounting.setSuiteNo(stage.getPaymentOrderId());
			accounting.setSummary("无");
			accountings.add(accounting);
		});
		return accountings;
	}

	public List<UnfreezeAccounting> buildUnfreeznInstruction(List<PaymentStagePO> stages) {
		List<UnfreezeAccounting> accountings = new ArrayList<UnfreezeAccounting>();
		stages.forEach(stage -> {
			UnfreezeAccounting ac = new UnfreezeAccounting();
			ac.setAmount(stage.getOrderAmount());
			ac.setCurrencyCode("CNY");
			ac.setOrigBizNo(stage.buildFreeznId());
			ac.setSettlementTime(new Date());
			ac.setSummary("入款解冻");
			ac.setTargetAccount(TargetAccount.instance(stage.getPayerAccountId(), ""));
			accountings.add(ac);
		});
		return accountings;
	}

}
