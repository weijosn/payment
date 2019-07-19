package com.cdzg.money.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AcctountTileKey {

	private String accTitleCode;

	private int currencyType;

	public static AcctountTileKey instance(String titleCode, int currencyType) {
		AcctountTileKey t = new AcctountTileKey();
		t.accTitleCode = titleCode;
		t.currencyType = currencyType;
		return t;
	}
}
