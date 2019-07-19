package com.cdzg.money.route.service.cpcn.purse;

import com.alibaba.dubbo.config.annotation.Reference;
import com.cdzg.cpcn.req.PurseAccountRequestVo;
import com.cdzg.cpcn.service.CpcnService;
import com.cdzg.money.api.channel.request.OpenAccountRequest;
import com.cdzg.money.api.vo.response.member.MemberResponseVo;
import com.cdzg.money.route.service.cpcn.AbstractCpcnRegProtocol;
import org.springframework.stereotype.Component;

/**
 * @Description: 中金个人钱包会员注册实现
 * @Author : pc.huang
 * @Date : 2019-04-01 13:14
 */
@Component("CPCN_PURSE_PERSONAGE")
public class CpcnPersonageRegBiz extends AbstractCpcnRegProtocol {
    @Reference(version = "1.0.0", check = false, timeout = 999999999, retries = -1)
    private CpcnService cpcnService;

    @Override
    public PurseAccountRequestVo getPurseAccount(OpenAccountRequest openAccount, MemberResponseVo memberResponseVo) {
        PurseAccountRequestVo purseAccountRequestVo = new PurseAccountRequestVo();
        purseAccountRequestVo.setCustomerNature("0");
        return purseAccountRequestVo;
    }

    @Override
    public CpcnService getCpcnService() {
        return cpcnService;
    }

}
