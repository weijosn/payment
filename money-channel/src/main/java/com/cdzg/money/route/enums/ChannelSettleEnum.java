package com.cdzg.money.route.enums;

import com.cdzg.money.route.service.comm.ChannelCode;

/**
 * @Description: 渠道支付枚举类
 * @Author : pc.huang
 * @Date : 2019-04-01 13:24
 */
public enum ChannelSettleEnum {
    /**
     * 中金钱包支付
     */
    CPCN_PURSE_SETTLE(ChannelCode.CPCN, "CPCN_PURSE_SETTLE", ChannelCode.PURSE.shortValue()),
    /**
     * 中金三类户支付
     */
    CPCN_THREE_SETTLE(ChannelCode.CPCN, "CPCN_THREE_SETTLE", ChannelCode.THREE.shortValue()),
    /**
     * 通联钱包支付
     */
    ALLINPAY_PURSE_SETTLE(ChannelCode.ALLINPAY, "ALLINPAY_PURSE_SETTLE", ChannelCode.PURSE.shortValue()),
    /**
     * 通联三类户支付
     */
    ALLINPAY_THREE_SETTLE(ChannelCode.ALLINPAY, "ALLINPAY_THREE_SETTLE", ChannelCode.THREE.shortValue()),
    /**
     * 支付宝支付
     */
    ALIBABA_SETTLE(ChannelCode.ALI_PAY, "ALIBABA_SETTLE", ChannelCode.PURSE.shortValue()),
    /**
     * 微信支付
     */
    WX_PAY_SETTLE(ChannelCode.WX_PAY, "WX_PAY_SETTLE", ChannelCode.PURSE.shortValue());


    /**
     * 机构类型
     */
    private String instCode;
    /**
     * bean名称
     */
    private String bean;

    /**
     * 支付类型 0：钱包，1:三类户
     */
    private short grantType;

    ChannelSettleEnum(String instCode, String bean, short grantType) {
        this.instCode = instCode;
        this.bean = bean;
        this.grantType = grantType;
    }

    ChannelSettleEnum() {
    }

    /**
     * 获取对应的bean名称
     *
     * @param instCode
     * @return
     */
    public static String getBeanName(String instCode, short grantType) {
        ChannelSettleEnum[] values = values();
        for (ChannelSettleEnum value : values) {
            if (value.instCode.equals(instCode) && value.grantType == grantType) {
                return value.bean;
            }
        }
        return null;
    }

    public String getInstCode() {
        return instCode;
    }

    public void setInstCode(String instCode) {
        this.instCode = instCode;
    }

    public String getBean() {
        return bean;
    }

    public void setBean(String bean) {
        this.bean = bean;
    }}
