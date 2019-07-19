package com.cdzg.money.model.enums;

public enum TitleTypeEnum {

    Assets(1), Liability(2), Joint(3),;

    private Integer code;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    private TitleTypeEnum(Integer code) {
        this.code = code;
    }

    public static TitleTypeEnum getByCode(int code) {
        for (TitleTypeEnum type : TitleTypeEnum.values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        return null;
    }
}
