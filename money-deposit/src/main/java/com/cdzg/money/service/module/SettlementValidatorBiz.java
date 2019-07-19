package com.cdzg.money.service.module;

import java.util.List;

import org.springframework.stereotype.Component;

import com.cdzg.money.api.deposit.vo.settle.DebitCreditAccounting;
import com.cdzg.money.api.deposit.vo.settle.FreezeAccounting;
import com.cdzg.money.api.deposit.vo.settle.SettlementRequest;
import com.cdzg.money.api.deposit.vo.settle.TargetAccount;
import com.cdzg.money.api.deposit.vo.settle.UnfreezeAccounting;

@Component
public class SettlementValidatorBiz {

	public void validate(SettlementRequest settlementRequest) {
		validateTransRule(settlementRequest);
	}

	private void validateTransRule(SettlementRequest settlementRequest) {
		List<UnfreezeAccounting> unfreezeAccountings = settlementRequest.getUnfreezeAccountings();
		List<DebitCreditAccounting> debitCreditAccountings = settlementRequest.getDebitCreditAccountings();
		List<FreezeAccounting> freezeAccountings = settlementRequest.getFreezeAccountings();
		boolean isEmpty = true;
		if ((unfreezeAccountings != null && !unfreezeAccountings.isEmpty())
				|| (freezeAccountings != null && !freezeAccountings.isEmpty())) {
			isEmpty = false;
		}
		if (debitCreditAccountings != null && !debitCreditAccountings.isEmpty()) {
			isEmpty = false;
			validateDebitCreditAccountings(debitCreditAccountings);
		}
		if (isEmpty) {
			throw new IllegalArgumentException("the request instructions cannot be empty");
		}
	}

	private void validateDebitCreditAccountings(List<DebitCreditAccounting> debitCreditAccountings) {
		for (DebitCreditAccounting debitCreditAccounting : debitCreditAccountings) {
			TargetAccount debitAccount = debitCreditAccounting.getDebitAccount();
			TargetAccount creditAccount = debitCreditAccounting.getCreditAccount();
			if (debitAccount.getAccountId().equals(creditAccount.getAccountId())) {
				throw new IllegalArgumentException("the debitCreditAccountings's accountId cannot be the same");
			}
		}
	}

}
