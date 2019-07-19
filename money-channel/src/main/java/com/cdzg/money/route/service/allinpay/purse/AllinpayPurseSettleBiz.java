package com.cdzg.money.route.service.allinpay.purse;

import com.alibaba.dubbo.config.annotation.Reference;
import com.cdzg.allinpay.req.purse.apply.PursePaymentApplyItemRequestVo;
import com.cdzg.allinpay.req.purse.apply.PursePaymentApplyRequestVo;
import com.cdzg.allinpay.req.purse.notarize.PursePaymentNotarizeRequestVo;
import com.cdzg.allinpay.req.purse.ordercreate.PurseInsideOrderRequestVo;
import com.cdzg.allinpay.req.purse.single.PurseSingleGrantRequestVo;
import com.cdzg.allinpay.res.purse.PurseBaseResponseVo;
import com.cdzg.allinpay.res.purse.apply.PursePaymentApplyResponseVo;
import com.cdzg.allinpay.res.purse.notarize.PursePaymentNotarizeResponseVo;
import com.cdzg.allinpay.res.purse.ordercreate.PurseInsideOrderResponseVo;
import com.cdzg.allinpay.res.purse.single.PurseSingleGrantResponseVo;
import com.cdzg.allinpay.service.AllinpayService;
import com.cdzg.money.api.channel.common.ChannelResultCode;
import com.cdzg.money.api.channel.common.PartyInfo;
import com.cdzg.money.api.channel.request.ChannelSettleRequest;
import com.cdzg.money.api.channel.request.ChannelSettleResponse;
import com.cdzg.money.api.deposit.vo.account.AcctQueryResponse;
import com.cdzg.money.api.service.account.IAccountService;
import com.cdzg.money.api.service.grant.OrgUsersService;
import com.cdzg.money.api.service.member.MemberService;
import com.cdzg.money.api.vo.request.grant.OrgUsersRequestVo;
import com.cdzg.money.api.vo.request.member.MemberRequestVo;
import com.cdzg.money.api.vo.response.grant.OrgUsersGrantTypeResponseVo;
import com.cdzg.money.api.vo.response.member.MemberResponseVo;
import com.cdzg.money.mapper.AccountRelationMapper;
import com.cdzg.money.model.AccountRelation;
import com.cdzg.money.route.service.allinpay.AbstractAllinpaySettleProtocol;
import com.framework.utils.core.api.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * @Description: 通联钱包支付实现
 * @Author : pc.huang
 * @Date : 2019-04-03 13:11
 */
@Component("ALLINPAY_PURSE_SETTLE")
public class AllinpayPurseSettleBiz extends AbstractAllinpaySettleProtocol {

    @Reference(version = "1.0.0", check = false, timeout = 999999999, retries = -1)
    private IAccountService accountService;

    @Reference(version = "1.0.0", check = false, timeout = 999999999, retries = -1)
    private AllinpayService allinpayService;

    @Reference(version = "1.0.0", check = false, timeout = 999999999, retries = -1)
    private MemberService memberService;

    @Reference(version = "1.0.0", check = false, timeout = 999999999, retries = -1)
    private OrgUsersService orgUsersService;


    @Autowired
    private AccountRelationMapper accountRelationMapper;

    @Override
    public ChannelSettleResponse settle(ChannelSettleRequest channelSettle) {
        ChannelSettleResponse channelResponse = new ChannelSettleResponse();
        //查询会员信息
        MemberRequestVo memberRequestVo = new MemberRequestVo();

        //付款人
        PartyInfo payer = channelSettle.getPayer();
        memberRequestVo.setUid(payer.getMemberId());
        MemberResponseVo payerMember = memberService.selectOne(memberRequestVo);
        AccountRelation payerRelation = accountRelationMapper.queryByAccountNo(payer.getAccountNo());

        //收款人
        PartyInfo payee = channelSettle.getPayee();
        memberRequestVo.setUid(payee.getMemberId());
        MemberResponseVo payeeMember = memberService.selectOne(memberRequestVo);

        //金额
        String amount = String.valueOf(channelSettle.getAmount().multiply(BigDecimal.valueOf(100)));
        //判断支付方式为福利发放或者用户消费
        AcctQueryResponse account = accountService.findByAccountId(payer.getAccountNo());
        //付款用户为商户则为福利发放
        if (account.getAccount().getAccountType().getCode() == 2) {
            //获取用户对应的机构信息
            return pryForPerson(channelResponse, payeeMember, amount);
        } else {
            //否则为用户向商家付款
            return payForMerchant(channelSettle, channelResponse, payerMember, payerRelation, amount);
        }
    }

    /**
     * 商家向用户付款
     *
     * @param channelResponse
     * @param payeeMember
     * @param amount
     * @return
     */
    private ChannelSettleResponse pryForPerson(ChannelSettleResponse channelResponse, MemberResponseVo payeeMember, String amount) {
        OrgUsersRequestVo vo = new OrgUsersRequestVo();
        vo.setIdCard(payeeMember.getIdCard());
        OrgUsersGrantTypeResponseVo orgInfo = orgUsersService.queryOrgUsersGrantType(vo);

        PurseSingleGrantRequestVo req = new PurseSingleGrantRequestVo();
        //商户号待完善
        req.setMerchantsNo("008310107630117");
        req.setName(payeeMember.getRealName());
        req.setPhone(payeeMember.getPhoneNumber());
        req.setIdCard(payeeMember.getIdCard());
        req.setGrantMoney(amount);
        req.setGrantType("2");
        req.setPkOrg(orgInfo.getPkOrg());
        ApiResponse<PurseBaseResponseVo<PurseSingleGrantResponseVo>> response = allinpayService.purseSingleGrant(req);
        if (response.getCode() != 0) {
            channelResponse.setResultCode(ChannelResultCode.FAIL.getCode());
            channelResponse.setResultMessage(response.getData().getEnvelope().getBody().getResMsg());
            return channelResponse;
        }
        channelResponse.setResultCode(ChannelResultCode.SUCCED.getCode());
        channelResponse.setPaiedAmount(BigDecimal.valueOf(Long.valueOf(amount)));
        return channelResponse;
    }

    /**
     * 用户向商家付款
     *
     * @param channelSettle
     * @param channelResponse
     * @param payerMember
     * @param payerRelation
     * @param amount
     * @return
     */
    private ChannelSettleResponse payForMerchant(ChannelSettleRequest channelSettle, ChannelSettleResponse channelResponse, MemberResponseVo payerMember, AccountRelation payerRelation, String amount) {
        //获取用户对应的机构信息
        OrgUsersRequestVo vo = new OrgUsersRequestVo();
        vo.setIdCard(payerMember.getIdCard());
        OrgUsersGrantTypeResponseVo orgInfo = orgUsersService.queryOrgUsersGrantType(vo);
        /**
         * 创建内部订单
         */
        PurseInsideOrderRequestVo inside = new PurseInsideOrderRequestVo();
        inside.setOrderType("2");
        inside.setOperationType("06");
        inside.setOperationSubType("0610");
        inside.setOrderMoney(amount);
        inside.setUserNo(payerRelation.getRelationNo());
        inside.setPhone(payerMember.getPhoneNumber());
        inside.setMerchantOrderNo(channelSettle.getBizNo());
        //商户号待完善
        inside.setMerchantCode("008310107630117");
        ApiResponse<PurseBaseResponseVo<PurseInsideOrderResponseVo>> insideResponse = allinpayService.purseInsideOrder(inside);
        if (insideResponse.getCode() != 0) {
            channelResponse.setResultCode(ChannelResultCode.FAIL.getCode());
            channelResponse.setResultMessage(insideResponse.getData().getEnvelope().getBody().getResMsg());
            return channelResponse;
        }
        /**
         * 支付申请
         */
        PursePaymentApplyRequestVo apply = new PursePaymentApplyRequestVo();
        apply.setUserNo(payerRelation.getRelationNo());
        apply.setPhone(payerMember.getPhoneNumber());
        apply.setOrderNo(insideResponse.getData().getEnvelope().getBody().getResult().getOrderNo());
        PursePaymentApplyItemRequestVo applyItem = new PursePaymentApplyItemRequestVo();
        applyItem.setIntegralAmount(amount);
        apply.setPayInformation(applyItem);
        //商户号待完善
        apply.setMerchantsNo("008310107630117");

        apply.setPkOrg(orgInfo.getPkOrg());
        ApiResponse<PurseBaseResponseVo<PursePaymentApplyResponseVo>> applyResponse = allinpayService.pursePaymentApply(apply);
        if (applyResponse.getCode() != 0) {
            channelResponse.setResultCode(ChannelResultCode.FAIL.getCode());
            channelResponse.setResultMessage(applyResponse.getData().getEnvelope().getBody().getResMsg());
            return channelResponse;
        }
        /**
         * 支付确认
         */
        PursePaymentNotarizeRequestVo notarize = new PursePaymentNotarizeRequestVo();
        notarize.setUserNo(payerRelation.getRelationNo());
        notarize.setAuthenticationWay("3");
        notarize.setPkOrg(orgInfo.getPkOrg());
        notarize.setOrderNo(insideResponse.getData().getEnvelope().getBody().getResult().getOrderNo());
        notarize.setPayOrderNo(applyResponse.getData().getEnvelope().getBody().getResult().getPayOrderNo());
        ApiResponse<PurseBaseResponseVo<PursePaymentNotarizeResponseVo>> notarizeResponse = allinpayService.pursePaymentNotarize(notarize);
        if (notarizeResponse.getCode() != 0) {
            channelResponse.setResultCode(ChannelResultCode.FAIL.getCode());
            channelResponse.setResultMessage(notarizeResponse.getData().getEnvelope().getBody().getResMsg());
            return channelResponse;
        }
        channelResponse.setResultCode(ChannelResultCode.SUCCED.getCode());
        channelResponse.setPaiedAmount(channelSettle.getAmount());
        return channelResponse;
    }


}
