package com.cdzg.money.route.service.cpcn.three;

import com.alibaba.dubbo.config.annotation.Reference;
import com.cdzg.cpcn.req.BankAccount;
import com.cdzg.cpcn.req.PaymentAccount;
import com.cdzg.cpcn.req.Tx4530RequestVo;
import com.cdzg.cpcn.res.Tx4530ResponseVo;
import com.cdzg.cpcn.service.CpcnService;
import com.cdzg.money.api.channel.common.ChannelResultCode;
import com.cdzg.money.api.channel.request.ChannelSettleRequest;
import com.cdzg.money.api.channel.request.ChannelSettleResponse;
import com.cdzg.money.api.service.base.WalletService;
import com.cdzg.money.api.vo.response.base.WalletResponseVo;
import com.cdzg.money.mapper.BizTxMapper;
import com.cdzg.money.model.BizTx;
import com.cdzg.money.route.service.cpcn.AbstractCpcnSettleProtocol;
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
 * @Description: 中金支付三类户单笔代付
 * @Author : pc.huang
 * @Date : 2019-04-02 11:09
 */
@Component("CPCN_THREE_SETTLE")
public class CpcnThreeSinglePayment extends AbstractCpcnSettleProtocol {

    @Reference(version = "1.0.0", check = false, timeout = 999999999, retries = -1)
    private CpcnService cpcnService;

    @Reference(version = "1.0.0", check = false, timeout = 999999999, retries = -1)
    private WalletService walletService;

    @Autowired
    private BizTxMapper bizTxMapper;

    @Override
    public ChannelSettleResponse settle(ChannelSettleRequest channelSettle) {
        ChannelSettleResponse channelSettleResponse = new ChannelSettleResponse();
        Tx4530RequestVo requestVo = new Tx4530RequestVo();

        //付款人
        PaymentAccount requestPayer = new PaymentAccount();
        //付款人账号信息待完善
        requestPayer.setPaymentAccountNumber("");
        requestPayer.setPaymentAccountName("");
        requestVo.setPayer(requestPayer);

        //收款人
        Map<String, Object> map = new HashMap<>();
        map.put("uid", channelSettle.getPayee().getMemberId());
        List<WalletResponseVo> list = walletService.queryData(map);
        if (ListUtil.isNotNull(list)) {
            WalletResponseVo walletResponseVo = list.get(0);
            BankAccount requestPayee = new BankAccount();
            requestPayee.setAccountNumber(walletResponseVo.getSubAccNo());
            requestPayee.setAccountName(walletResponseVo.getUserName());
            requestPayee.setAccountType(11);
            requestPayee.setPhoneNumber(walletResponseVo.getPhone());
            requestPayee.setBankID("306");
            requestVo.setPayee(requestPayee);
        }
        //金额
        requestVo.setAmount(channelSettle.getAmount().multiply(BigDecimal.valueOf(100)).longValue());
        //代付标识
        requestVo.setPaymentFlag("1");
        ApiResponse<Tx4530ResponseVo> response = cpcnService.singlePayment(requestVo);
        if (response.getCode() == 200 && response.getData().getStatus() == 20) {
            channelSettleResponse.setResultCode(ChannelResultCode.SUCCED.getCode());
            channelSettleResponse.setPaiedAmount(channelSettle.getAmount());
        } else {
            channelSettleResponse.setResultCode(ChannelResultCode.FAIL.getCode());
            channelSettleResponse.setResultMessage(response.getData().getResponseMessage());
        }

        //保存订单号与交易流水号之间的关系
        BizTx bizTx = new BizTx();
        bizTx.setBizNo(channelSettle.getBizNo());
        bizTx.setPkBizTx(UuidUtil.getUpperId());
        bizTx.setTxSn(response.getData().getTxSN());
        bizTx.setInstCode(channelSettle.getTargetInstCode());
        bizTx.setCreatTime(new Date());
        bizTx.setGrantType(1);
        bizTxMapper.save(bizTx);

        return channelSettleResponse;
    }

}
