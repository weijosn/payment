package com.cdzg.money.api.deposit.vo.settle;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * 结算请求 储值在一个事务内完成 1,按序执行解冻指令 2,按序执行借贷指令 3,按序执行冻结指令
 *
 */
@Getter
@Setter
public class SettlementRequest extends com.cdzg.money.api.deposit.vo.DepositBaseRequest {

	private static final long serialVersionUID = 7227705292252642442L;

	private List<UnfreezeAccounting> unfreezeAccountings;

	private List<DebitCreditAccounting> debitCreditAccountings;

	private List<FreezeAccounting> freezeAccountings;

}
