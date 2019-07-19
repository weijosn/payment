package com.cdzg.money.route.enums;

import com.cdzg.money.route.service.comm.ChannelCode;

/**
 * @Description: 渠道注册枚举类
 * @Author : pc.huang
 * @Date : 2019-04-01 13:24
 */
public enum ChannelRegEnum {
    /**
     * 中金钱包个人用户注册
     */
    CPCN_PURSE_PERSONAGE(ChannelCode.PERSONAGE, ChannelCode.CPCN, "CPCN_PURSE_PERSONAGE"),
    /**
     * 中金钱包商户用户注册
     */
    CPCN_PURSE_MERCHANT(ChannelCode.MERCHANT, ChannelCode.CPCN, "CPCN_PURSE_MERCHANT"),
    /**
     * 通联钱包个人用户注册
     */
    ALLINPAY_PURSE_PERSONAGE(ChannelCode.PERSONAGE, ChannelCode.ALLINPAY, "ALLINPAY_PURSE_PERSONAGE"),
    /**
     * 通联钱包商户用户注册
     */
    ALLINPAY_PURSE_MERCHANT(ChannelCode.MERCHANT, ChannelCode.ALLINPAY, "ALLINPAY_PURSE_MERCHANT"),
    ;

    /**
     * 用户类型 1:个人用户，2：商户用户
     */
    private Integer type;
    /**
     * 机构类型
     */
    private String instCode;
    /**
     * bean名称
     */
    private String bean;

    ChannelRegEnum(Integer type, String instCode, String bean) {
        this.type = type;
        this.instCode = instCode;
        this.bean = bean;
    }


    ChannelRegEnum() {
    }

    /**
     * 获取对应的bean名称
     *
     * @param type
     * @return
     */
    public static String getBeanName(Integer type, String instCode) {
        ChannelRegEnum[] values = values();
        for (ChannelRegEnum value : values) {
            if (value.type.equals(type) && value.instCode.equals(instCode)) {
                return value.bean;
            }
        }
        return null;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getBean() {
        return bean;
    }

    public void setBean(String bean) {
        this.bean = bean;
    }}
