package com.cdzg.money.route.service.cpcn.purse;

import com.alibaba.dubbo.config.annotation.Reference;
import com.cdzg.cpcn.req.PurseAccountRequestVo;
import com.cdzg.cpcn.service.CpcnService;
import com.cdzg.money.api.channel.request.OpenAccountRequest;
import com.cdzg.money.api.vo.response.member.MemberResponseVo;
import com.cdzg.money.route.service.cpcn.AbstractCpcnRegProtocol;
import org.springframework.stereotype.Component;

/**
 * @Description: 中金商户钱包会员注册实现
 * @Author : pc.huang
 * @Date : 2019-04-01 13:14
 */
@Component("CPCN_PURSE_MERCHANT")
public class CpcnMerchantRegBiz extends AbstractCpcnRegProtocol {
    @Reference(version = "1.0.0", check = false, timeout = 999999999, retries = -1)
    private CpcnService cpcnService;

    @Override
    public PurseAccountRequestVo getPurseAccount(OpenAccountRequest openAccount, MemberResponseVo member) {
        PurseAccountRequestVo purseAccountRequestVo = new PurseAccountRequestVo();
        purseAccountRequestVo.setCustomerNature("1");
        //商户营业执照等待完善
        purseAccountRequestVo.setCreditCode("");
        purseAccountRequestVo.setOrgCode("");
        purseAccountRequestVo.setBusinessLicense("");
        purseAccountRequestVo.setRegistrationNumber("");
        return purseAccountRequestVo;
    }

    @Override
    public CpcnService getCpcnService() {
        return cpcnService;
    }

}
