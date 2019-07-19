package com.cdzg.money.api.deposit.vo.account;

public enum AccountTypeEnum {

    Individual(1), 
    
    Merchant(2), 
    
    Internal(3);

    private int code;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    private AccountTypeEnum(int code) {
        this.code = code;
    }

    public static AccountTypeEnum getByCode(int code) {
        for (AccountTypeEnum type : AccountTypeEnum.values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        return null;
    }
}
