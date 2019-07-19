package com.cdzg.money.route.service.allinpay;

import com.cdzg.allinpay.req.purse.register.PurseRegMembersRequestVo;
import com.cdzg.allinpay.res.purse.PurseBaseResponseVo;
import com.cdzg.allinpay.res.purse.register.PurseRegMembersResponseVo;
import com.cdzg.allinpay.service.AllinpayService;
import com.cdzg.money.api.channel.common.ChannelResultCode;
import com.cdzg.money.api.channel.request.OpenAccountRequest;
import com.cdzg.money.api.channel.request.OpenAccountResponse;
import com.cdzg.money.api.vo.response.member.MemberResponseVo;
import com.cdzg.money.mapper.AccountRelationMapper;
import com.cdzg.money.model.AccountRelation;
import com.cdzg.money.route.service.comm.AbstractProtocol;
import com.cdzg.money.utils.UuidUtil;
import com.framework.utils.core.api.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

/**
 * @Description: 通联会员注册抽象协议类
 * @Author : pc.huang
 * @Date : 2019-04-03 09:40
 */
public abstract class AbstractAllinpayRegProtocol extends AbstractProtocol {

    @Autowired
    private AccountRelationMapper accountRelationMapper;

    /**
     * 获取通联服务
     *
     * @return
     */
    public abstract AllinpayService getAllinpayService();

    /**
     * 获取通联请求对象
     *
     * @return
     */
    public abstract PurseRegMembersRequestVo getAllinpayReq(MemberResponseVo member);

    @Override
    public OpenAccountResponse doAccount(OpenAccountRequest openAccount, MemberResponseVo member) {
        OpenAccountResponse openAccountResponse = new OpenAccountResponse();
        PurseRegMembersRequestVo allinpayReq = this.getAllinpayReq(member);
        allinpayReq.setRegType("1");
        allinpayReq.setRegSource("2023");
        allinpayReq.setPhone(member.getPhoneNumber());
        allinpayReq.setIpAddress("192.168.1.1");

        ApiResponse<PurseBaseResponseVo<PurseRegMembersResponseVo>> response = getAllinpayService().purseRegisteredMembers(allinpayReq);
        //开户成功存入关联数据库
        if (response.getCode() == 200) {
            String userNo = response.getData().getEnvelope().getBody().getResult().getUserNo();
            AccountRelation accountRelation = new AccountRelation();
            accountRelation.setPkAccountRelation(UuidUtil.getUpperId());
            accountRelation.setCreateTime(new Date());
            accountRelation.setAccountNo(openAccount.getPartyInfo().getAccountNo());
            accountRelation.setRelationNo(userNo);
            accountRelation.setRelationType(Integer.valueOf(member.getThirdPayType()));
            accountRelation.setDesc(response.getData().getEnvelope().getBody().getResMsg());
            accountRelationMapper.save(accountRelation);
            openAccountResponse.setVirtualAccountNo(userNo);
        } else {
            openAccountResponse.setResultCode(ChannelResultCode.REGIEST_ERROR.getCode());
            openAccountResponse.setResultMessage(response.getData().getEnvelope().getBody().getResMsg());
        }
        return openAccountResponse;
    }

}
