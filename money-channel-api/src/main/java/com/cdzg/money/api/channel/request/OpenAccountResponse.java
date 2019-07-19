package com.cdzg.money.api.channel.request;

import com.cdzg.money.api.channel.BaseResponse;

import lombok.Getter;
import lombok.Setter;

/**
 * 用户注册响应对象
 *
 * @author appple
 */
@Getter
@Setter
public class OpenAccountResponse extends BaseResponse {

    private static final long serialVersionUID = -8578802566213980425L;

    /**
     * 第三方支付机构账户
     */
    private String virtualAccountNo;

}
