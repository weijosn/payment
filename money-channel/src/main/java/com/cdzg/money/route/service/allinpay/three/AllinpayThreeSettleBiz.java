package com.cdzg.money.route.service.allinpay.three;

import com.alibaba.dubbo.config.annotation.Reference;
import com.cdzg.allinpay.req.three.single.SinglePayment;
import com.cdzg.allinpay.res.three.single.SinglePaymentResponseVo;
import com.cdzg.allinpay.service.AllinpayService;
import com.cdzg.money.api.channel.common.ChannelResultCode;
import com.cdzg.money.api.channel.common.PartyInfo;
import com.cdzg.money.api.channel.request.ChannelSettleRequest;
import com.cdzg.money.api.channel.request.ChannelSettleResponse;
import com.cdzg.money.api.service.base.WalletService;
import com.cdzg.money.api.service.grant.OrgUsersService;
import com.cdzg.money.api.service.member.MemberService;
import com.cdzg.money.api.vo.request.grant.OrgUsersRequestVo;
import com.cdzg.money.api.vo.request.member.MemberRequestVo;
import com.cdzg.money.api.vo.response.base.WalletResponseVo;
import com.cdzg.money.api.vo.response.grant.OrgUsersGrantTypeResponseVo;
import com.cdzg.money.api.vo.response.member.MemberResponseVo;
import com.cdzg.money.mapper.BizTxMapper;
import com.cdzg.money.model.BizTx;
import com.cdzg.money.route.service.allinpay.AbstractAllinpaySettleProtocol;
import com.cdzg.money.utils.ListUtil;
import com.cdzg.money.utils.UuidUtil;
import com.framework.utils.core.api.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 通联三类户支付实现
 * @Author : pc.huang
 * @Date : 2019-04-03 16:43
 */
@Component("ALLINPAY_THREE_SETTLE")
public class AllinpayThreeSettleBiz extends AbstractAllinpaySettleProtocol {

    @Reference(version = "1.0.0", check = false, timeout = 999999999, retries = -1)
    private AllinpayService allinpayService;

    @Reference(version = "1.0.0", check = false, timeout = 999999999, retries = -1)
    private WalletService walletService;

    @Reference(version = "1.0.0", check = false, timeout = 999999999, retries = -1)
    private OrgUsersService orgUsersService;

    @Reference(version = "1.0.0", check = false, timeout = 999999999, retries = -1)
    private MemberService memberService;

    @Autowired
    private BizTxMapper bizTxMapper;

    @Override
    public ChannelSettleResponse settle(ChannelSettleRequest channelSettle) {
        ChannelSettleResponse response = new ChannelSettleResponse();
        //查询会员信息
        MemberRequestVo memberRequestVo = new MemberRequestVo();
        PartyInfo payer = channelSettle.getPayer();
        memberRequestVo.setUid(payer.getMemberId());
        MemberResponseVo payerMember = memberService.selectOne(memberRequestVo);
        //查询付款人对应的机构信息
        OrgUsersRequestVo vo = new OrgUsersRequestVo();
        vo.setIdCard(payerMember.getIdCard());
        OrgUsersGrantTypeResponseVo orgInfo = orgUsersService.queryOrgUsersGrantType(vo);
        SinglePayment requestVo = new SinglePayment();
        String pkOrg = orgInfo.getPkOrg();
        requestVo.setPkOrg(pkOrg);
        requestVo.setAccountProp("0");
        requestVo.setAmount(String.valueOf(channelSettle.getAmount().multiply(BigDecimal.valueOf(100))));
        requestVo.setBankCode("0306");
        //收款人
        Map<String, Object> map = new HashMap<>();
        map.put("uid", channelSettle.getPayee().getMemberId());
        List<WalletResponseVo> list = walletService.queryData(map);
        if (ListUtil.isNotNull(list)) {
            WalletResponseVo walletResponseVo = list.get(0);
            requestVo.setAccountNo(walletResponseVo.getUserName());
            requestVo.setAccountNo(walletResponseVo.getSubAccNo());
        }
        ApiResponse<SinglePaymentResponseVo> paymentResponse = allinpayService.singlePayment(requestVo);
        if ("0000".equals(paymentResponse.getData().getInfoReturnResponseVo().getRetCode())) {
            response.setResultCode(ChannelResultCode.SUCCED.getCode());
            response.setPaiedAmount(channelSettle.getAmount());
        } else {
            response.setResultCode(ChannelResultCode.FAIL.getCode());
            response.setResultMessage(paymentResponse.getData().getTransReturnResponseVo().getErrMsg());
        }
        //保存订单号与交易流水号之间的关系
        BizTx bizTx = new BizTx();
        bizTx.setBizNo(channelSettle.getBizNo());
        bizTx.setPkBizTx(UuidUtil.getUpperId());
        bizTx.setTxSn(paymentResponse.getData().getInfoReturnResponseVo().getReqSn());
        bizTx.setInstCode(channelSettle.getTargetInstCode());
        bizTx.setCreatTime(new Date());
        bizTx.setPkOrg(pkOrg);
        bizTx.setGrantType(1);
        bizTxMapper.save(bizTx);
        return response;
    }
}
