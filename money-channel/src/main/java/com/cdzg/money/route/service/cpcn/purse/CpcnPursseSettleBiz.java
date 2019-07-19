package com.cdzg.money.route.service.cpcn.purse;

import com.alibaba.dubbo.config.annotation.Reference;
import com.cdzg.cpcn.req.PursePayRequestVo;
import com.cdzg.cpcn.res.PursePayResponseVo;
import com.cdzg.cpcn.service.CpcnService;
import com.cdzg.money.api.channel.common.ChannelResultCode;
import com.cdzg.money.api.channel.common.PartyInfo;
import com.cdzg.money.api.channel.request.ChannelSettleRequest;
import com.cdzg.money.api.channel.request.ChannelSettleResponse;
import com.cdzg.money.api.service.member.MemberService;
import com.cdzg.money.api.vo.request.member.MemberRequestVo;
import com.cdzg.money.api.vo.response.member.MemberResponseVo;
import com.cdzg.money.mapper.AccountRelationMapper;
import com.cdzg.money.mapper.BizTxMapper;
import com.cdzg.money.model.AccountRelation;
import com.cdzg.money.model.BizTx;
import com.cdzg.money.route.service.cpcn.AbstractCpcnSettleProtocol;
import com.cdzg.money.utils.UuidUtil;
import com.framework.utils.core.api.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description: 中金钱包支付实现类
 * @Author : pc.huang
 * @Date : 2019-04-01 15:38
 */
@Slf4j
@Component("CPCN_PURSE_SETTLE")
public class CpcnPursseSettleBiz extends AbstractCpcnSettleProtocol {

    @Reference(version = "1.0.0", check = false, timeout = 999999999, retries = -1)
    private CpcnService cpcnService;

    @Reference(version = "1.0.0", check = false, timeout = 999999999, retries = -1)
    private MemberService memberService;

    @Autowired
    private AccountRelationMapper accountRelationMapper;

    @Autowired
    private BizTxMapper bizTxMapper;

    @Override
    public ChannelSettleResponse settle(ChannelSettleRequest channelSettle) {
        ChannelSettleResponse channelSettleResponse = new ChannelSettleResponse();
        //中金
        //查询会员信息
        MemberRequestVo memberRequestVo = new MemberRequestVo();

        PursePayRequestVo pursePayRequestVo = new PursePayRequestVo();
        //付款方
        PartyInfo payer = channelSettle.getPayer();
        AccountRelation payerAccountRelation = accountRelationMapper.queryByAccountNo(payer.getAccountNo());
        pursePayRequestVo.setPaymentAccountNo(payerAccountRelation.getRelationNo());
        memberRequestVo.setUid(channelSettle.getPayer().getMemberId());
        MemberResponseVo payerMember = memberService.selectOne(memberRequestVo);
        pursePayRequestVo.setPaymentAccountName(payerMember.getRealName());
        //收款方
        PartyInfo payee = channelSettle.getPayee();
        AccountRelation payeeAccountRelation = accountRelationMapper.queryByAccountNo(payee.getAccountNo());
        pursePayRequestVo.setCollectionAccountNo(payeeAccountRelation.getRelationNo());
        memberRequestVo.setUid(channelSettle.getPayee().getMemberId());
        MemberResponseVo payeeMember = memberService.selectOne(memberRequestVo);
        pursePayRequestVo.setCollectionAccountName(payeeMember.getRealName());
        //付款金额
        pursePayRequestVo.setAmount(channelSettle.getAmount().multiply(BigDecimal.valueOf(100)).longValue());
        pursePayRequestVo.setTrsflag("A00");
        ApiResponse<PursePayResponseVo> response = cpcnService.pursePay(pursePayRequestVo);
        if (response.getCode() == 200 && response.getData().getCode().equals("0000")) {
            channelSettleResponse.setPaiedAmount(channelSettle.getAmount());
            channelSettleResponse.setResultCode(ChannelResultCode.SUCCED.getCode());
        } else {
            channelSettleResponse.setResultCode(ChannelResultCode.PAYMENT_ERROR.getCode());
            channelSettleResponse.setResultMessage(response.getData().getMsg());
        }

        //保存订单号与交易流水号之间的关系
        BizTx bizTx = new BizTx();
        bizTx.setBizNo(channelSettle.getBizNo());
        bizTx.setPkBizTx(UuidUtil.getUpperId());
        bizTx.setTxSn(response.getData().getBillNo());
        bizTx.setInstCode(channelSettle.getTargetInstCode());
        bizTx.setCreatTime(new Date());
        bizTx.setGrantType(0);
        bizTxMapper.save(bizTx);
        return channelSettleResponse;
    }
}
