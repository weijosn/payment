package com.cdzg.money.route.service.comm;

import com.cdzg.money.api.channel.request.ChannelSettleRequest;
import com.cdzg.money.api.channel.request.ChannelSettleResponse;
import com.cdzg.money.api.channel.request.OpenAccountRequest;
import com.cdzg.money.api.channel.request.OpenAccountResponse;
import com.cdzg.money.api.vo.response.member.MemberResponseVo;
import com.cdzg.money.model.BizTx;

/**
 * @Description: 渠道服务抽象类
 * @Author : pc.huang
 * @Date : 2019-04-03 10:24
 */
public abstract class AbstractProtocol implements ChannelProtocol {
    @Override
    public OpenAccountResponse doAccount(OpenAccountRequest openAccount, MemberResponseVo member) {
        return null;
    }

    @Override
    public ChannelSettleResponse settle(ChannelSettleRequest channelSettle) {
        return null;
    }

    @Override
    public ChannelSettleResponse query(String orderNo) {
        return null;
    }

    @Override
    public OpenAccountResponse openAccount(OpenAccountRequest openAccount) {
        return null;
    }

    @Override
    public ChannelSettleResponse doQuery(BizTx bizTx) {
        return null;
    }
}
