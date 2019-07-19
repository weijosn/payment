package com.cdzg.money.model.enums;

public enum TransactionStatusEnum {

	Valid(1,"正常"),
	Invalid(2,"不正常"),
	Pending(0,"进行中")
	;
	
	private final int code;
	private final String desc;

	TransactionStatusEnum(int code, String desc) {
		this.code = code;
		this.desc = desc;
		
	}

	public int getCode() {
		return code;
	}

	public String getDesc() {
		return desc;
	}
	
	public static TransactionStatusEnum getByCode(int code) {
        for (TransactionStatusEnum type : TransactionStatusEnum.values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        return null;
    }
}
