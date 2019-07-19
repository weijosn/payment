package com.cdzg.money.route.service.comm;

/**
 * @Description: 渠道编码静态常量类
 * @Author : pc.huang
 * @Date : 2019-04-08 14:39
 */
public class ChannelCode {
    /**
     * 通联支付
     */
    public static final String ALLINPAY = "111";
    /**
     * 中金支付
     */
    public static final String CPCN = "222";
    /**
     * 支付宝
     */
    public static final String ALI_PAY = "333";
    /**
     * 微信
     */
    public static final String WX_PAY = "444";

    /**
     * 个人用户注册
     */
    public static final Integer PERSONAGE = 1;
    /**
     * 商户用户注册
     */
    public static final Integer MERCHANT = 2;
    /**
     * 支付类型：钱包
     */
    public static final Integer PURSE = 0;
    /**
     * 支付类型：三类户
     */
    public static final Integer THREE = 1;
}
