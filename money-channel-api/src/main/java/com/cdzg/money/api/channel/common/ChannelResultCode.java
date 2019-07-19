package com.cdzg.money.api.channel.common;

import java.io.Serializable;

/**
 * @author appple
 */

public enum ChannelResultCode implements Serializable {

    SUCCED("1000", "成功"),


    // 请求参数非法
    ILLEGAL_ARGUMENT("2001", "请求参数非法"),

    // 请求按流程做完，明确失败,客户端无需关注失败原因
    FAIL("9001", "请求处理失败"),

    /**
     * 注册通联会员异常
     */
    REGIEST_ERROR("5001", "注册中金会员异常"),

    /**
     * 支付异常
     */
    PAYMENT_ERROR("5003", "支付异常"),
    /**
     * 未查询到对应bean对象
     */
    BEAN_NOT_FOUND_ERROR("3001", "未查询到对应bean对象"),
    /**
     * 未查询到对应渠道信息
     */
    CHANNEL_NOT_FOUND_ERROR("3002", "未查询到对应渠道信息"),
    /**
     * 获取bean异常
     */
    GAIN_BEAN_ERROR("3003", "获取bean异常"),
    /**
     * 未查询到相关会员信息
     */
    MEMBER_NOT_FOUNT_ERROR("3004", "未查询到相关会员信息"),
    /**
     * 未查询到相关交易信息
     */
    TX_NOT_FOUNT_ERROR("3005", "未查询到相关交易信息"),
    ;

    private String code;

    private String message;

    private ChannelResultCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    /**
     * 通过代码获取枚举项
     *
     * @param code
     * @return
     */
    public static ChannelResultCode getByCode(String code) {
        if (code == null || code.trim().length() == 0) {
            return null;
        }

        for (ChannelResultCode errorCode : ChannelResultCode.values()) {
            if (errorCode.getCode().equals(code)) {
                return errorCode;
            }
        }
        return null;
    }
}
