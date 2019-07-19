package com.cdzg.money.api.channel.request;

import java.math.BigDecimal;

import com.cdzg.money.api.channel.BaseResponse;
import com.cdzg.money.api.channel.common.Extension;

import lombok.Getter;
import lombok.Setter;

/**
 * 用户支付响应对象
 *
 * @author appple
 */
@Getter
@Setter
public class ChannelSettleResponse extends BaseResponse {

    private static final long serialVersionUID = -1197612210791333337L;

    /**
     * 支付金额
     */
    private BigDecimal paiedAmount;

    /**
     * 扩展字段
     */
    private Extension extension;

}
