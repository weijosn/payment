package com.cdzg.money.route.enums;

import com.cdzg.money.route.service.comm.ChannelCode;

/**
 * @Description: 渠道查询枚举类
 * @Author : pc.huang
 * @Date : 2019-04-01 13:24
 */
public enum ChannelQueryEnum {
    /**
     * 中金钱包支付查询
     */
    CPCN_PURSE_QUERY(ChannelCode.CPCN, "CPCN_PURSE_QUERY", 0),
    /**
     * 中金三类户支付查询
     */
    CPCN_THREE_QUERY(ChannelCode.CPCN, "CPCN_THREE_QUERY", 1),
    /**
     * 通联钱包支付查询
     */
    ALLINPAY_PURSE_QUERY(ChannelCode.ALLINPAY, "ALLINPAY_PURSE_QUERY", 0),
    /**
     * 通联三类户支付查询
     */
    ALLINPAY_THREE_QUERY(ChannelCode.ALLINPAY, "ALLINPAY_THREE_QUERY", 1),
    /**
     * 支付宝支付查询
     */
    ALIBABA_QUERY(ChannelCode.ALI_PAY, "ALIBABA_QUERY", 1);


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
    private int grantType;

    ChannelQueryEnum(String instCode, String bean, int grantType) {
        this.instCode = instCode;
        this.bean = bean;
        this.grantType = grantType;
    }

    ChannelQueryEnum() {
    }

    /**
     * 获取对应的bean名称
     *
     * @param instCode
     * @return
     */
    public static String getBeanName(String instCode, int grantType) {
        ChannelQueryEnum[] values = values();
        for (ChannelQueryEnum value : values) {
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
