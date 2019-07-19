package com.cdzg.money.model;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.util.Assert;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentInstruction implements java.io.Serializable {

	private static final long serialVersionUID = 7119855387358912403L;

	/**
	 * 应付总额
	 */
	private BigDecimal totalAmount;

	/**
	 * 会员id
	 */
	private String memberId;

	/**
	 * 回调地址
	 */
	private String returnURL;
	/**
	 * 通知地址
	 */
	private String notifyURL;
	/**
	 * 分阶段支付
	 */
	private List<StageInstruction> stages;

	public void validate() {

		// 这里肯定是能过的，因为存在0元支付，所以先这样。不返回错误
		Assert.isTrue(stages.size() > 0, "支付指令为空！");

		// 统计总额
		BigDecimal total = BigDecimal.ZERO;
		for (StageInstruction si : this.stages) {
			total = total.add(si.getAmount());
		}
		
		// 总额和明细必须是一致的
		Assert.isTrue(totalAmount.compareTo(total) == 0, "支付金额不正确");

		// 确认支付金额是正确的
		Assert.isTrue(totalAmount.compareTo(BigDecimal.ZERO) >= 0, "支付金额必须是正数");

	}

}
