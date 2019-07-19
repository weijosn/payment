package com.cdzg.money.route.service.cpcn;

import com.cdzg.cpcn.req.PurseAccountRequestVo;
import com.cdzg.cpcn.res.PurseAccountResponseVo;
import com.cdzg.cpcn.service.CpcnService;
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
 * @Description: 中金会员注册协议类
 * @Author : pc.huang
 * @Date : 2019-04-02 15:34
 */
public abstract class AbstractCpcnRegProtocol extends AbstractProtocol {
    @Autowired
    private AccountRelationMapper accountRelationMapper;

    /**
     * 获取中金会员注册请求参数
     *
     * @param openAccount
     * @param member
     * @return
     */
    public abstract PurseAccountRequestVo getPurseAccount(OpenAccountRequest openAccount, MemberResponseVo member);

    /**
     * 获取中金服务
     *
     * @return
     */
    public abstract CpcnService getCpcnService();


    @Override
    public OpenAccountResponse doAccount(OpenAccountRequest openAccount, MemberResponseVo member) {
        OpenAccountResponse openAccountResponse = new OpenAccountResponse();
        PurseAccountRequestVo purseAccount = this.getPurseAccount(openAccount, member);
        purseAccount.setCustomerNo(openAccount.getPartyInfo().getAccountNo());
        purseAccount.setName(member.getRealName());
        purseAccount.setIdCard(member.getIdCard());
        purseAccount.setMobole(member.getPhoneNumber());
        purseAccount.setIdType("A");
        purseAccount.setFcflg("1");
        purseAccount.setAcctp("1");
        ApiResponse<PurseAccountResponseVo> response = this.getCpcnService().purseAccount(purseAccount);
        //开户成功存入关联数据库
        if (response.getCode() == 200) {
            AccountRelation accountRelation = new AccountRelation();
            accountRelation.setPkAccountRelation(UuidUtil.getUpperId());
            accountRelation.setCreateTime(new Date());
            accountRelation.setAccountNo(openAccount.getPartyInfo().getAccountNo());
            accountRelation.setRelationNo(response.getData().getAccountNo());
            accountRelation.setRelationType(Integer.valueOf(member.getThirdPayType()));
            accountRelation.setDesc(response.getData().getMsg());
            accountRelationMapper.save(accountRelation);
            openAccountResponse.setVirtualAccountNo(response.getData().getAccountNo());
        } else {
            openAccountResponse.setResultCode(ChannelResultCode.REGIEST_ERROR.getCode());
            openAccountResponse.setResultMessage(response.getData().getMsg());
        }
        return openAccountResponse;
    }
}
