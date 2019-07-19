package com.cdzg.money.api.deposit.vo.account;

import com.cdzg.money.api.deposit.vo.DepositBaseRequest;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AcctStatusChangeRequest extends DepositBaseRequest {

	private static final long serialVersionUID = 3369937064265341662L;

	private String accountId;

    /**
     * 0:未激活 1:激活 2:锁定 3:止出 4:止入 5:注销
     */
    private AccountStatusEnum targetStatus;


}
