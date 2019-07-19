package com.cdzg.money.api.channel.request;

import com.cdzg.money.api.channel.BaseRequest;
import com.cdzg.money.api.channel.common.Extension;
import com.cdzg.money.api.channel.common.PartyInfo;

import lombok.Getter;
import lombok.Setter;

/**
 * 账户注册请求对象
 *
 * @author appple
 */
@Getter
@Setter
public class OpenAccountRequest extends BaseRequest {

    private static final long serialVersionUID = 5119423194486288562L;

    /**
     * 用户信息
     */
    private PartyInfo partyInfo;

    /**
     * 渠道来源
     */
    private String targetInstCode;

    /**
     * 扩展字段
     */
    private Extension extension;

    /**
     * 用户类型 1:个人用户，2：商户用户
     */
    private int accountType;

}
