package com.cdzg.money.service.module.channel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.dubbo.config.annotation.Reference;
import com.cdzg.money.api.channel.common.PartyInfo;
import com.cdzg.money.api.channel.request.ChannelSettleRequest;
import com.cdzg.money.api.channel.request.ChannelSettleResponse;
import com.cdzg.money.api.channel.settle.IChannelService;
import com.cdzg.money.api.deposit.vo.account.AcctQueryResponse;
import com.cdzg.money.api.service.account.IAccountService;
import com.cdzg.money.api.service.settle.ISettlementService;
import com.cdzg.money.mapper.PaymentStageMapper;
import com.cdzg.money.mapper.entity.PaymentStagePO;

@Component
public class DefaultProtocolBiz extends AbstractSettlementProtocol {

    @Autowired
    private PaymentStageMapper paymentStageMapper;

    @Reference(version = "1.0.0", check = false, timeout = 999999999, retries = -1)
    private ISettlementService settlementService;

    @Reference(version = "1.0.0", check = false, timeout = 999999999, retries = -1)
    private IAccountService accountService;

    @Reference(version = "1.0.0", check = false, timeout = 999999999, retries = -1)
    private IChannelService channelService;

    public ChannelSettleResponse stageSettle(long stageId) {

        PaymentStagePO stage = this.paymentStageMapper.findById(stageId);

        AcctQueryResponse payee = this.accountService.findByAccountId(stage.getPayeeAccountId());
        AcctQueryResponse payer = this.accountService.findByAccountId(stage.getPayerAccountId());

        ChannelSettleRequest channelSettle = new ChannelSettleRequest();
        channelSettle.setAppId("payment");

        channelSettle.setBizNo(String.valueOf(stage.getId()));

        channelSettle.setPayee(PartyInfo.instance(payee.getAccount().getAccountNo(), payee.getAccount().getMemberId()));
        channelSettle.setPayer(PartyInfo.instance(payer.getAccount().getAccountNo(), payer.getAccount().getMemberId()));
        channelSettle.setAmount(stage.getOrderAmount());

        ChannelSettleResponse response = null;
        try {
        	response = channelService.settle(channelSettle);
        }catch(Exception e){
        	
        }finally {
        	response = new ChannelSettleResponse();
        	response.setResultCode("1000");
        	response.setPaiedAmount(stage.getOrderAmount());
        }
        
        return response;

    }

    @Override
    public ChannelSettleResponse query(long stageId) {
        PaymentStagePO stage = this.paymentStageMapper.findById(stageId);
        ChannelSettleResponse response = channelService.query(String.valueOf(stage.getId()));
        return response;
    }

}
