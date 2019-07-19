package com.cdzg.money.api.payment.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentPartyInfo implements java.io.Serializable {
    private static final long serialVersionUID = -7868004882686240572L;
    
    /**付款方信息：非空*/
    private PartyInfo           payer;
    
    /**收款方信息：非空/*/
    private PartyInfo           payee;

    /**业务发起方信息：充值=payee 提现=payer 支付基于业务来定*/
    private PartyInfo           bizInitiator;


}
