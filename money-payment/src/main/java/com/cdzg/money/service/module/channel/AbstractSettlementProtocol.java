package com.cdzg.money.service.module.channel;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cdzg.money.api.channel.request.ChannelSettleResponse;
import com.cdzg.money.api.payment.request.Extension;
import com.cdzg.money.mapper.PaymentStageMapper;
import com.cdzg.money.mapper.entity.PaymentStagePO;
import com.cdzg.money.model.enums.OrderStatusEnum;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractSettlementProtocol implements ChannelSettleProtocol {

    @Autowired
    private PaymentStageMapper paymentStageMapper;

    public abstract ChannelSettleResponse stageSettle(long stageId);

    public abstract ChannelSettleResponse query(long stageId);

    public StageResult doSettle(long stageId) {

        PaymentStagePO stageOrder = paymentStageMapper.findById(stageId);
        log.info("do settlement protocol , order id {} stage id {}", stageOrder.getPaymentOrderId(), stageOrder.getId());
        final StageResult stageResult = new StageResult();

        try {

            ChannelSettleResponse response = null;

            // 查状态
            if (OrderStatusEnum.getByCode(stageOrder.getOrderStatus()) == OrderStatusEnum.SUCCESS) {
                stageResult.setResult(StageResultEnum.SUCCESS);
                return stageResult;
            } else if (OrderStatusEnum.getByCode(stageOrder.getOrderStatus()) == OrderStatusEnum.PENDING) {
                response = this.query(stageId);
            } else if (paymentStageMapper.updateToPending(stageId, new Date(), OrderStatusEnum.INIT.getCode()) == 1) {
                response = stageSettle(stageId);
            } else {
                stageResult.setResult(StageResultEnum.FAILED);
                return stageResult;
            }

            if (response == null || StringUtils.isEmpty(response.getResultCode())) {
            	log.error("do settlement protocol , order id {} response empty!",stageOrder.getPaymentOrderId());
                stageResult.setResult(StageResultEnum.FAILED);
                return stageResult;
            }

            log.info("do settlement protocol , order id {} response {}", stageOrder.getPaymentOrderId(), response);

			// 判断结果(ChannelResultCode)
			switch (response.getResultCode()) {
			case "1000": // 订单支付成功
				if (_succed(response.getPaiedAmount(), stageOrder)) {
					stageResult.setResult(StageResultEnum.SUCCESS);
				} else {
					stageResult.setResult(StageResultEnum.WAIT);
				}
				break;
			case "1001": // 需要异步通知
				if (_wait(stageOrder)) {
					stageResult.setResult(StageResultEnum.WAIT);
				} else {
					stageResult.setResult(StageResultEnum.WAIT);
				}
				break;
			case "9001": // 明确失败
				if (_failed(stageOrder)) {
					stageResult.setResult(StageResultEnum.FAILED);
				} else {
					stageResult.setResult(StageResultEnum.WAIT);
				}
				break;
			default:
				stageResult.setResult(StageResultEnum.WAIT);
			}
            stageResult.setExtension(toPaymentExtension(response.getExtension()));
        } catch (Exception e) {
            log.error("stage settlement error", e);
            stageResult.setResult(StageResultEnum.WAIT);
        }

        return stageResult;

    }

	// 返回透传的参数
    private Extension toPaymentExtension(com.cdzg.money.api.channel.common.Extension extension) {
    	if(extension==null || extension.getEntries()==null) {
    		return null;
    	}
        com.cdzg.money.api.payment.request.Extension ex = new com.cdzg.money.api.payment.request.Extension();
        for (com.cdzg.money.api.channel.common.Extension.Kvp kvp : extension.getEntries()) {
            ex.addExtProperty(com.cdzg.money.api.payment.request.Extension.Kvp.instance(kvp.getKey(), kvp.getValue()));
        }
        return ex;
    }

    private boolean _wait(PaymentStagePO stageOrder) {
        return 1 == paymentStageMapper.updateToInit(stageOrder.getId(), OrderStatusEnum.PENDING.getCode());
    }

    private boolean _failed(PaymentStagePO stageOrder) {
    	return 1 == paymentStageMapper.updateToInit(stageOrder.getId(), OrderStatusEnum.FAILED.getCode());
	}

    private boolean _succed(BigDecimal paiedAmount, PaymentStagePO stageOrder) {

        // 判断金额是否一致
        if (paiedAmount.compareTo(stageOrder.getOrderAmount()) < 0 ) {
            log.warn("settle money error , order id {} stage id {}", stageOrder.getPaymentOrderId(), stageOrder.getId());
            return false;
        }

        // 更新结算结果
        return paymentStageMapper.updateToSucced(stageOrder.getId(),
                paiedAmount,
                new Date(),
                OrderStatusEnum.PENDING.getCode()) == 1;

    }

}
