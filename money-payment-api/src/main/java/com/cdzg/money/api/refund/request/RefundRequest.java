package com.cdzg.money.api.refund.request;

import java.math.BigDecimal;

import com.cdzg.money.api.payment.PaymentBaseRequest;
import com.cdzg.money.api.payment.request.Extension;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RefundRequest extends PaymentBaseRequest {

	private static final long serialVersionUID = 5233055279698929062L;

	/** 退款金额，若退款金额等于原充值金额，标识全额退款，若小于原始金额，标识部分退款 不可空 */
	private BigDecimal refundAmount;

	/** 冻结标识：若调用者设为true，标识该笔走冻结退款，反之走直连退款 不可空默认是false */
	//private Boolean freezeFlag = Boolean.FALSE;

	/** 支付过程中遇到需要回调及通知的时候的返回地址 */
	private String callbackAddress;

	/** 退款扩展信息 */
	private Extension extension;

	private String paymentOrderId;
	
	private String remark;

}
