package com.cdzg.money.model;

/**
 * 支付上下文线程局部变量封装
 */
public class PaymentContextHolder {

	private static ThreadLocal<PaymentContext> threadVar = new ThreadLocal<PaymentContext>();

	/**
	 * 获得支付上下文实例，先从当前线程上下文取，如为空，新建一个
	 * 
	 * @return
	 */
	public static PaymentContext getInstance() {
		PaymentContext ctx = threadVar.get();
		if (ctx == null) {
			ctx = new PaymentContext();
			threadVar.set(ctx);
		}
		return ctx;
	}

	/**
	 * 获得支付上下文，如线程上下文中无实例，返回空
	 * 
	 * @return
	 */
	public static PaymentContext getPaymentContext() {
		return threadVar.get();
	}

	/**
	 * 清除支付上下文
	 */
	public static void clearPaymentContext() {
		if (threadVar.get() != null) {
			threadVar.set(null);
		}
		threadVar.remove();
	}

}
