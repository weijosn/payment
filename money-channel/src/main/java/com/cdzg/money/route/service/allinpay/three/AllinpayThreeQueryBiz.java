package com.cdzg.money.route.service.allinpay.three;

import com.alibaba.dubbo.config.annotation.Reference;
import com.cdzg.allinpay.req.three.payquery.PaymentQueryRequestVo;
import com.cdzg.allinpay.res.three.payquery.PaymentQueryResponseVo;
import com.cdzg.allinpay.service.AllinpayService;
import com.cdzg.money.api.channel.common.ChannelResultCode;
import com.cdzg.money.api.channel.request.ChannelSettleResponse;
import com.cdzg.money.model.BizTx;
import com.cdzg.money.route.service.comm.AbstractProtocol;
import com.framework.utils.core.api.ApiResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * @Description: 通联三类户支付查询
 * @Author : pc.huang
 * @Date : 2019-04-08 13:38
 */
@Component("ALLINPAY_THREE_QUERY")
public class AllinpayThreeQueryBiz extends AbstractProtocol {

    @Reference(version = "1.0.0", check = false, timeout = 999999999, retries = -1)
    private AllinpayService allinpayService;

    @Override
    public ChannelSettleResponse doQuery(BizTx bizTx) {
        ChannelSettleResponse response = new ChannelSettleResponse();
        PaymentQueryRequestVo query = new PaymentQueryRequestVo();
        query.setQuerySn(bizTx.getTxSn());
        query.setPkOrg(bizTx.getPkOrg());
        ApiResponse<PaymentQueryResponseVo> queryResponse = allinpayService.paymentQuery(query);
        if ("0000".equals(queryResponse.getData().getInfoReturnResponseVo().getRetCode())) {
            response.setResultCode(ChannelResultCode.SUCCED.getCode());
            response.setPaiedAmount(new BigDecimal(queryResponse.getData().getQtDetailReturnResponseVos().get(0).getAmount()));
        } else {
            response.setResultCode(ChannelResultCode.FAIL.getCode());
            response.setResultMessage(queryResponse.getData().getInfoReturnResponseVo().getErrMsg());
        }
        return response;
    }
}
