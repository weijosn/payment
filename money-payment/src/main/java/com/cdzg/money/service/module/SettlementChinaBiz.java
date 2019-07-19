package com.cdzg.money.service.module;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cdzg.money.exception.PaymentException;
import com.cdzg.money.exception.PaymentResultCode;
import com.cdzg.money.exception.SuspendSettleException;
import com.cdzg.money.mapper.PaymentStageMapper;
import com.cdzg.money.mapper.entity.PaymentStagePO;
import com.cdzg.money.model.PaymentContext;
import com.cdzg.money.model.PaymentContextHolder;
import com.cdzg.money.model.enums.PaymentCodeEnum;
import com.cdzg.money.service.module.channel.ChannelSettleProtocol;
import com.cdzg.money.service.module.channel.ChannelSettleProtocol.StageResult;
import com.cdzg.money.service.module.channel.ChannelSettleProtocol.StageResultEnum;
import com.cdzg.money.service.module.deposit.DepositSettleProtocol;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 执行内部及外部结算动作
 * @author jiangwei
 *
 */
@Component
@Slf4j
public class SettlementChinaBiz {

	@Autowired
	private DepositSettleProtocol depositSettleProtocol;
    @Autowired
    private ChannelSettleProtocol channelSettleProtocol;
	@Autowired
	private PaymentStageMapper paymentStageMapper;

	public SettlementChinaInstruction build(PaymentCodeEnum paymentCode) {
		return new SettlementChinaInstruction(paymentCode);
	}

	/**
	 * 结算链对象
	 * @author jiangwei
	 *
	 */
	@Getter
	public class SettlementChinaInstruction {

		private PaymentCodeEnum paymentCode;
		private List<PaymentStagePO> stages;
		private PaymentContext paymentContext;

		SettlementChinaInstruction(PaymentCodeEnum paymentCode) {
			PaymentContext paymentContext = PaymentContextHolder.getInstance();
			this.paymentCode = paymentCode;
			this.paymentContext = PaymentContextHolder.getInstance();
			this.stages = paymentStageMapper.listByOrderId(paymentContext.getPaymentOrderId());
		}

		public SettlementChinaInstruction freezn() {
			// 支付、出款需要冻结
			if (hasFreezn()) {
				depositSettleProtocol.freezn(this);
			}
			return this;
		}
		
		public SettlementChinaInstruction channelSettle() {
	        for(PaymentStagePO stage:stages) {
	            StageResult stageResult = channelSettleProtocol.doSettle(stage.getId());
	            // 渠道失败-中断
	            if (stageResult == null || stageResult.getResult() == StageResultEnum.FAILED) {
	                log.error("payment instruction runtime error.channel settlement failed.orderId {}", paymentContext.getPaymentOrderId());
	                throw new PaymentException(PaymentResultCode.CHANNEL_SETTLEMENT_ERROR, PaymentResultCode.CHANNEL_SETTLEMENT_ERROR.getMessage());
	            }
	            // 中断执行
	            if(stageResult.getResult() != StageResultEnum.SUCCESS) {
	            	log.warn("payment instruction runtime suspend.orderId {}",paymentContext.getPaymentOrderId());
	            	throw new SuspendSettleException();
	            }
	        }
			return this;
		}

		public SettlementChinaInstruction settle() {
			depositSettleProtocol.settle(this);
			return this;
		}

		public boolean hasFreezn() {
			return paymentContext.getPaymentCode() == PaymentCodeEnum.PAYMENT || paymentContext.getPaymentCode() == PaymentCodeEnum.REFUND;
		}

	}

}
