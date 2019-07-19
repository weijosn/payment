package com.cdzg.money.route.service.comm;

import com.cdzg.money.api.channel.request.ChannelSettleResponse;
import com.cdzg.money.api.channel.request.OpenAccountRequest;
import com.cdzg.money.api.channel.request.OpenAccountResponse;
import com.cdzg.money.api.channel.settle.IChannelService;
import com.cdzg.money.api.vo.response.member.MemberResponseVo;
import com.cdzg.money.model.BizTx;

/**
 * @Description: 渠道协议类
 * @Author : pc.huang
 * @Date : 2019-04-01 13:07
 */
public interface ChannelProtocol extends IChannelService {

    /**
     * 会员注册
     *
     * @param openAccount
     * @param member
     * @return
     */
    OpenAccountResponse doAccount(OpenAccountRequest openAccount, MemberResponseVo member);

    /**
     * 支付查询
     *
     * @param bizTx
     * @return
     */
    ChannelSettleResponse doQuery(BizTx bizTx);
}
