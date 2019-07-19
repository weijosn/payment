package com.cdzg.money.route.service.allinpay.purse;

import com.alibaba.dubbo.config.annotation.Reference;
import com.cdzg.allinpay.req.purse.register.PurseRegMembersRequestVo;
import com.cdzg.allinpay.service.AllinpayService;
import com.cdzg.money.api.service.grant.OrgUsersService;
import com.cdzg.money.api.vo.request.grant.OrgUsersRequestVo;
import com.cdzg.money.api.vo.response.grant.OrgUsersGrantTypeResponseVo;
import com.cdzg.money.api.vo.response.member.MemberResponseVo;
import com.cdzg.money.route.service.allinpay.AbstractAllinpayRegProtocol;
import org.springframework.stereotype.Component;

/**
 * @Description: 通联商户用户钱包注册服务实现
 * @Author : pc.huang
 * @Date : 2019-04-02 09:14
 */
@Component("ALLINPAY_PURSE_MERCHANT")
public class AllinpayMerchantRegBiz extends AbstractAllinpayRegProtocol {

    @Reference(version = "1.0.0", check = false, timeout = 999999999, retries = -1)
    private AllinpayService allinpayService;

    @Reference(version = "1.0.0", check = false, timeout = 999999999, retries = -1)
    private OrgUsersService orgUsersService;

    @Override
    public AllinpayService getAllinpayService() {
        return allinpayService;
    }

    @Override
    public PurseRegMembersRequestVo getAllinpayReq(MemberResponseVo member) {
        PurseRegMembersRequestVo membersRequestVo = new PurseRegMembersRequestVo();
        OrgUsersRequestVo vo = new OrgUsersRequestVo();
        vo.setIdCard(member.getIdCard());
        OrgUsersGrantTypeResponseVo orgInfo = orgUsersService.queryOrgUsersGrantType(vo);
        membersRequestVo.setPkOrg(orgInfo.getPkOrg());
        membersRequestVo.setMerchantsMemberNo("");
        membersRequestVo.setMerchantsNo("");
        return membersRequestVo;
    }

}
