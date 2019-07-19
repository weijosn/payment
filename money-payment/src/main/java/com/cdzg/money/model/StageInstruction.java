package com.cdzg.money.model;

import java.math.BigDecimal;

import com.cdzg.money.api.payment.request.PartyInfo;

import lombok.Getter;

@Getter
public class StageInstruction implements java.io.Serializable {

	private static final long serialVersionUID = -7468827367093724776L;

	private BigDecimal amount;

	private PartyInfo payee;

	private PartyInfo payer;

	public static StageInstruction newState(BigDecimal amount, PartyInfo payer, PartyInfo payee) {
		StageInstruction ps = new StageInstruction();
		ps.amount = amount;
		ps.payer = payer;
		ps.payee = payee;
		return ps;
	}

}
