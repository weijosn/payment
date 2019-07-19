package com.cdzg.money.route.service.wxpay;

import com.cdzg.money.api.channel.request.ChannelSettleRequest;
import com.cdzg.money.api.channel.request.ChannelSettleResponse;
import com.cdzg.money.route.service.comm.AbstractProtocol;
import org.springframework.stereotype.Component;

/**
 * @Description: 微信支付实现
 * @Author : pc.huang
 * @Date : 2019-04-09 13:06
 */
@Component("WX_PAY_SETTLE")
public class WxPayBiz extends AbstractProtocol {

    @Override
    public ChannelSettleResponse settle(ChannelSettleRequest channelSettle) {
        ChannelSettleResponse response = new ChannelSettleResponse();
        return response;
    }
}
