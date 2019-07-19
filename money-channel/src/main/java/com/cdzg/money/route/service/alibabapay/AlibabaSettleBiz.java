package com.cdzg.money.route.service.alibabapay;

import com.alibaba.dubbo.config.annotation.Reference;
import com.cdzg.alipay.api.service.AlipayUtilsService;
import com.cdzg.alipay.api.vo.request.PayToAccountRequest;
import com.cdzg.money.api.channel.common.ChannelResultCode;
import com.cdzg.money.api.channel.common.PartyInfo;
import com.cdzg.money.api.channel.request.ChannelSettleRequest;
import com.cdzg.money.api.channel.request.ChannelSettleResponse;
import com.cdzg.money.api.service.base.ConfigService;
import com.cdzg.money.api.service.base.ShopStoreService;
import com.cdzg.money.route.service.comm.AbstractProtocol;
import com.framework.utils.core.api.ApiResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * @Description: 支付宝支付实现类
 * @Author : pc.huang
 * @Date : 2019-04-04 09:05
 */
@Component("ALIBABA_SETTLE")
public class AlibabaSettleBiz extends AbstractProtocol {

    @Reference(version = "1.0.0", check = false, timeout = 999999999, retries = -1)
    private AlipayUtilsService alipayUtilsService;

    @Reference(version = "1.0.0", check = false, timeout = 999999999, retries = -1)
    private ShopStoreService shopStoreService;

    @Reference(version = "1.0.0", check = false, timeout = 999999999, retries = -1)
    private ConfigService configService;

    @Override
    public ChannelSettleResponse settle(ChannelSettleRequest channelSettle) {

        ChannelSettleResponse channelSettleResponse = new ChannelSettleResponse();

        //付款人
        PartyInfo payer = channelSettle.getPayer();
        String shopId = payer.getMemberId();
        //查询商户信息
        PartyInfo payee = channelSettle.getPayee();

        //金额
        BigDecimal amount = channelSettle.getAmount();

        PayToAccountRequest payToAccountRequest = new PayToAccountRequest();
        payToAccountRequest.setAmount(amount.longValue());
        //配置付款方
        payToAccountRequest.setConfigId(payer.getMemberId());
        payToAccountRequest.setToAccountOrderId(channelSettle.getBizNo());
        payToAccountRequest.setPayType(1);
        payToAccountRequest.setPayTypeName("支付宝支付");
        //配置收款账户
        payToAccountRequest.setPayeeAccount(payee.getMemberId());
        payToAccountRequest.setToAccountReason(channelSettle.getExtension().getEntries().get(0).getValue());
        ApiResponse<Boolean> response = alipayUtilsService.orderToAccount(payToAccountRequest);
        if (!response.getData()) {
            channelSettleResponse.setResultCode(ChannelResultCode.FAIL.getCode());
            return channelSettleResponse;
        }
        channelSettleResponse.setResultCode(ChannelResultCode.SUCCED.getCode());
        channelSettleResponse.setPaiedAmount(channelSettle.getAmount());
        return channelSettleResponse;
    }
}
