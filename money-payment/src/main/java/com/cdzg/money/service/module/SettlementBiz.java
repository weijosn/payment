package com.cdzg.money.service.module;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.alibaba.dubbo.config.annotation.Reference;
import com.cdzg.money.api.service.settle.ISettlementService;
import com.cdzg.money.exception.PaymentException;
import com.cdzg.money.exception.SuspendSettleException;
import com.cdzg.money.mapper.PaymentOrderMapper;
import com.cdzg.money.mapper.PaymentStageMapper;
import com.cdzg.money.mapper.entity.PaymentOrderPO;
import com.cdzg.money.mapper.entity.PaymentStagePO;
import com.cdzg.money.model.PaymentContext;
import com.cdzg.money.model.PaymentContextHolder;
import com.cdzg.money.model.enums.PaymentCodeEnum;
import com.cdzg.money.service.listener.AsynNotifyEvent;
import com.cdzg.money.service.module.SettlementChinaBiz.SettlementChinaInstruction;

@Component
public class SettlementBiz {

    @Autowired
    private PaymentStageMapper paymentStageMapper;
    @Autowired
    private PaymentOrderMapper paymentOrderMapper;
    @Reference(version = "1.0.0", check = false, timeout = 999999999, retries = -1)
    private ISettlementService settlementService;
    @Autowired
    private SettlementChinaBiz settlementChinaBiz;
    @Autowired
    private ApplicationContext context;

    public void settle() throws SuspendSettleException , PaymentException{

        PaymentContext paymentContext = PaymentContextHolder.getInstance();
        PaymentOrderPO paymentOrder = paymentOrderMapper.findByOrderId(paymentContext.getPaymentOrderId());
        final PaymentCodeEnum paymentCode = PaymentCodeEnum.getByCode(paymentOrder.getPaymentCode());

        Assert.isTrue(paymentCode!=null,"指定的支付编码不存在");
        
		/**
		 * 根据支付编码执行对应的协议<br>
		 * 理想做法是配置协议，目前采用硬编码
		 */
		SettlementChinaInstruction china = settlementChinaBiz.build(paymentCode);
		// 进行退款解冻
		// if (paymentCode == PaymentCodeEnum.REFUND) {
		//			china.unfreezn();
		// }
		// 进行正常的资金操作流程
		china.freezn().channelSettle().settle();
    }

    // 更新数据
    public void clearing() {
        PaymentContext paymentContext = PaymentContextHolder.getInstance();
        PaymentOrderPO paymentOrder = paymentOrderMapper.findByTradeNo(paymentContext.getTradeNo(), paymentContext.getAppId());
        List<PaymentStagePO> stages = paymentStageMapper.listByOrderId(paymentOrder.getPaymentOrderId());
        BigDecimal paiedTotalAmount = stages.stream().map(PaymentStagePO::getPaiedAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (paiedTotalAmount.compareTo(paymentOrder.getOrderAmount()) >= 0) {
            paymentOrderMapper.updateToSucced(paymentOrder.getId(), paiedTotalAmount, new Date());
            context.publishEvent(new AsynNotifyEvent(paymentOrder.getPaymentOrderId()));
        } else if (paiedTotalAmount.compareTo(BigDecimal.ZERO) > 0) {
            paymentOrderMapper.updateToPartyPaied(paymentOrder.getId(), paiedTotalAmount, new Date());
        }
    }

}
