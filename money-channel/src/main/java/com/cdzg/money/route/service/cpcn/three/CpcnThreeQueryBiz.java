package com.cdzg.money.route.service.cpcn.three;

import com.alibaba.dubbo.config.annotation.Reference;
import com.cdzg.cpcn.req.Tx4532RequestVo;
import com.cdzg.cpcn.res.Tx4532ResponseVo;
import com.cdzg.cpcn.service.CpcnService;
import com.cdzg.money.api.channel.common.ChannelResultCode;
import com.cdzg.money.api.channel.request.ChannelSettleResponse;
import com.cdzg.money.model.BizTx;
import com.cdzg.money.route.service.comm.AbstractProtocol;
import com.framework.utils.core.api.ApiResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * @Description: 中金支付三类户支付查询
 * @Author : pc.huang
 * @Date : 2019-04-08 13:58
 */
@Component("CPCN_THREE_QUERY")
public class CpcnThreeQueryBiz extends AbstractProtocol {

    @Reference(version = "1.0.0", check = false, timeout = 999999999, retries = -1)
    private CpcnService cpcnService;

    @Override
    public ChannelSettleResponse doQuery(BizTx bizTx) {
        ChannelSettleResponse response = new ChannelSettleResponse();
        Tx4532RequestVo query = new Tx4532RequestVo();
        query.setTxSN(bizTx.getTxSn());
        ApiResponse<Tx4532ResponseVo> queryResponse = cpcnService.singlePaymentQuery(query);
        if (queryResponse.getCode() == 200 && queryResponse.getData().getStatus() == 30) {
            response.setResultCode(ChannelResultCode.SUCCED.getCode());
            response.setPaiedAmount(BigDecimal.valueOf(queryResponse.getData().getAmount()));
        } else {
            response.setResultCode(ChannelResultCode.FAIL.getCode());
            response.setResultMessage(queryResponse.getData().getResponseMessage());
        }
        return response;
    }
}
