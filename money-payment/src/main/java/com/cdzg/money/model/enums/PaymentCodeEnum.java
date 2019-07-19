package com.cdzg.money.model.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * 产品码
 *
 * @author jiangwei
 */
public enum PaymentCodeEnum {

    FUND_IN("FUND_IN", "入款"),

    REFUND("REFUND", "退款"),

    PAYMENT("TRADE", "交易");

    private final String code;
    private final String desc;

    PaymentCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;

    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static PaymentCodeEnum getByCode(String code) {
        for (PaymentCodeEnum type : PaymentCodeEnum.values()) {
            if (StringUtils.equals(code, type.getCode())) {
                return type;
            }
        }
        return null;
    }
}
