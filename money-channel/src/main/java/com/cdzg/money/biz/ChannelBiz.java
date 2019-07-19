package com.cdzg.money.biz;

import com.alibaba.dubbo.config.annotation.Reference;
import com.cdzg.money.api.channel.common.ChannelResultCode;
import com.cdzg.money.api.channel.request.ChannelSettleRequest;
import com.cdzg.money.api.channel.request.ChannelSettleResponse;
import com.cdzg.money.api.channel.request.OpenAccountRequest;
import com.cdzg.money.api.channel.request.OpenAccountResponse;
import com.cdzg.money.api.service.grant.OrgUsersService;
import com.cdzg.money.api.service.member.MemberService;
import com.cdzg.money.api.vo.request.grant.OrgUsersRequestVo;
import com.cdzg.money.api.vo.request.member.MemberRequestVo;
import com.cdzg.money.api.vo.response.grant.OrgUsersGrantTypeResponseVo;
import com.cdzg.money.api.vo.response.member.MemberResponseVo;
import com.cdzg.money.exception.ChannelException;
import com.cdzg.money.mapper.AccountRelationMapper;
import com.cdzg.money.mapper.BizTxMapper;
import com.cdzg.money.model.AccountRelation;
import com.cdzg.money.model.BizTx;
import com.cdzg.money.route.enums.ChannelQueryEnum;
import com.cdzg.money.route.service.comm.ChannelRouteBiz;
import com.cdzg.money.route.enums.ChannelRegEnum;
import com.cdzg.money.route.enums.ChannelSettleEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Description: 渠道服务实现
 * @Author : pc.huang
 * @Date : 2019-03-29 14:41
 */
@Service
public class ChannelBiz {

    @Autowired
    private AccountRelationMapper accountRelationMapper;

    @Autowired
    private ChannelRouteBiz channelRouteBiz;

    @Reference(version = "1.0.0", check = false, timeout = 999999999, retries = -1)
    private OrgUsersService orgUsersService;

    @Reference(version = "1.0.0", check = false, timeout = 999999999, retries = -1)
    private MemberService memberService;

    @Autowired
    private BizTxMapper bizTxMapper;


    public OpenAccountResponse openAccount(OpenAccountRequest open) {
        OpenAccountResponse openAccountResponse = new OpenAccountResponse();
        //已注册会员直接返回对应信息
        AccountRelation relation = accountRelationMapper.queryByAccountNo(open.getPartyInfo().getAccountNo());
        if (relation != null) {
            openAccountResponse.setVirtualAccountNo(relation.getRelationNo());
            openAccountResponse.setResultCode(ChannelResultCode.SUCCED.getCode());
            return openAccountResponse;
        }
        //查询会员信息
        MemberRequestVo memberRequestVo = new MemberRequestVo();
        memberRequestVo.setUid(open.getPartyInfo().getMemberId());
        MemberResponseVo member = memberService.selectOne(memberRequestVo);
        if (member == null) {
            throw new ChannelException(ChannelResultCode.MEMBER_NOT_FOUNT_ERROR);
        }
        return channelRouteBiz.route(ChannelRegEnum.getBeanName(open.getAccountType(), open.getTargetInstCode())).doAccount(open, member);
    }

    public ChannelSettleResponse settle(ChannelSettleRequest channelSettle) {
        //查询会员信息
        MemberRequestVo memberRequestVo = new MemberRequestVo();
        memberRequestVo.setUid(channelSettle.getPayee().getMemberId());
        MemberResponseVo memberResponseVo = memberService.selectOne(memberRequestVo);
        if (memberResponseVo == null) {
            throw new ChannelException(ChannelResultCode.MEMBER_NOT_FOUNT_ERROR);
        }
        //获取支付类型 钱包/三类户
        OrgUsersRequestVo vo = new OrgUsersRequestVo();
        vo.setIdCard(memberResponseVo.getIdCard());
        OrgUsersGrantTypeResponseVo typeResponseVo = orgUsersService.queryOrgUsersGrantType(vo);

        return channelRouteBiz.route(ChannelSettleEnum.getBeanName(channelSettle.getTargetInstCode(), typeResponseVo.getGrantType())).settle(channelSettle);
    }

    public ChannelSettleResponse query(String orderNo) {
        BizTx bizTx = bizTxMapper.queryObject(orderNo);
        if (bizTx != null) {
            throw new ChannelException(ChannelResultCode.TX_NOT_FOUNT_ERROR);
        }
        return channelRouteBiz.route(ChannelQueryEnum.getBeanName(bizTx.getInstCode(), bizTx.getGrantType())).doQuery(bizTx);
    }
}
