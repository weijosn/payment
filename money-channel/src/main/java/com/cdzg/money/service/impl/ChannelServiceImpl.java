package com.cdzg.money.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.dubbo.config.annotation.Service;
import com.cdzg.money.api.channel.request.ChannelSettleRequest;
import com.cdzg.money.api.channel.request.ChannelSettleResponse;
import com.cdzg.money.api.channel.request.OpenAccountRequest;
import com.cdzg.money.api.channel.request.OpenAccountResponse;
import com.cdzg.money.api.channel.settle.IChannelService;
import com.cdzg.money.biz.ChannelBiz;

/**
 * 支付业务对外接口
 *
 * @author jiangwei
 */
@Service(version = "1.0.0", timeout = 999999999, retries = -1, filter = {"exceptionFilter"})
@Component
public class ChannelServiceImpl implements IChannelService {

    @Autowired
    private ChannelBiz channelBiz;

    @Override
    public OpenAccountResponse openAccount(OpenAccountRequest openAccount) {
        return channelBiz.openAccount(openAccount);
    }

    @Override
    public ChannelSettleResponse settle(ChannelSettleRequest channelSettle) {
        return channelBiz.settle(channelSettle);
    }

    @Override
    public ChannelSettleResponse query(String orderNo) {
        return channelBiz.query(orderNo);
    }

}
