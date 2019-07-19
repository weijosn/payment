package com.cdzg.money.api.deposit.vo.account;

public enum AccountStatusEnum {

	/**
	 * 0:未激活 1:激活 2:锁定 3:止出 4:止入 5:注销
	 */

	Inactive(0), Active(1), Locked(2), FundOutRefused(3), FundInRefused(4), Cancelled(5),;

	private int code;

	private AccountStatusEnum(Integer code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public static AccountStatusEnum getByCode(int code) {
		for (AccountStatusEnum type : AccountStatusEnum.values()) {
			if (code == type.getCode()) {
				return type;
			}
		}
		return null;
	}
}
