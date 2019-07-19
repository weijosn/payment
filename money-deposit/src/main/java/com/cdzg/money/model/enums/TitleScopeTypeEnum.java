package com.cdzg.money.model.enums;

public enum TitleScopeTypeEnum {

    External(1), Internal(2),;

    private Integer code;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    private TitleScopeTypeEnum(Integer code) {
        this.code = code;
    }

    public static TitleScopeTypeEnum getByCode(int code) {
        for (TitleScopeTypeEnum type : TitleScopeTypeEnum.values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        return null;
    }
}
