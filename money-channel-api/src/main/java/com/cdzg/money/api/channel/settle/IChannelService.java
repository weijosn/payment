package com.cdzg.money.api.channel.settle;

import com.cdzg.money.api.channel.request.ChannelSettleRequest;
import com.cdzg.money.api.channel.request.ChannelSettleResponse;
import com.cdzg.money.api.channel.request.OpenAccountRequest;
import com.cdzg.money.api.channel.request.OpenAccountResponse;

/**
 * 渠道服务接口
 *
 * @author appple
 */
public interface IChannelService {

    /**
     * 第三方账户注册
     *
     * @param openAccount
     * @return
     */
    OpenAccountResponse openAccount(OpenAccountRequest openAccount);

    /**
     * 支付
     *
     * @param channelSettle
     * @return
     */
    ChannelSettleResponse settle(ChannelSettleRequest channelSettle);

    /**
     * 交易查询
     *
     * @param orderNo
     * @return
     */
    ChannelSettleResponse query(String orderNo);

}
