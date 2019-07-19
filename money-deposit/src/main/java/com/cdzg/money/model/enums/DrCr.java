package com.cdzg.money.model.enums;

public enum DrCr {

	Debit("O"), 

	Credit("I") ;

	;

	private String code;

	public String getCode() {
		return code;
	}

	private DrCr(String code) {
		this.code = code;
	}

	public static DrCr getByCode(String code) {
		for (DrCr type : DrCr.values()) {
			if (type.getCode().equals(code)) {
				return type;
			}
		}
		return null;
	}
}
