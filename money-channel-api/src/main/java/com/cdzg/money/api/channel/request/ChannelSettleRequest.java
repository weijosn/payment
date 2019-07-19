package com.cdzg.money.api.channel.request;

import com.cdzg.money.api.channel.BaseRequest;
import com.cdzg.money.api.channel.common.Extension;
import com.cdzg.money.api.channel.common.PartyInfo;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 支付请求对象
 *
 * @author appple
 */
@Getter
@Setter
public class ChannelSettleRequest extends BaseRequest {

    private static final long serialVersionUID = 5119423194486288562L;

    /**
     * 付款人
     */
    private PartyInfo payer;

    /**
     * 收款人
     */
    private PartyInfo payee;

    /**
     * 扩展字段
     */
    private Extension extension;

    /**
     * 付款金额
     */
    private BigDecimal amount;

    /**
     * 渠道编码
     */
    private String targetInstCode;

}
