# 支付项目
有时间就写写，玩〜

## deposit
   储值服务，提供内部支付结算功能
   
   ### 账号借贷示例
   ```java
   	@Test
	public void fundIn() throws Exception {
		ArrayList<Object> list = Lists.newArrayList();
		int start = 20000;
		for (int i = start; i < start + 100; i++) {
			list.add(i);
		}
		AtomicInteger total = new AtomicInteger();
		String token = String.valueOf(System.currentTimeMillis());
		list.parallelStream().forEach(value -> {
			FundInRequest request = new FundInRequest();
			request.setAppId("111");
			request.setBizNo(String.valueOf(value));
			request.setAccount(TargetAccount.instance("31030016358687917375750295", "充值"));
			request.setAmount(BigDecimal.valueOf(10));
			request.setSuiteNo(token);
			DepositBaseResponse rep = settlementService.fundIn(request);
			System.out.println(rep.getResultCode()+"---"+rep.getResultMessage());
			total.addAndGet(10);
		});
		Thread.sleep(128 * 1000);
		System.out.println("增加的金额:" + total);
	}
   ```
 
## payment
   交易服务，提供交易指令处理功能。
   
   ''' 简单的例子
   
   ### 付款
   ```java
   @Test
	public void pay() throws Exception {
		this.paymentService.fundIn(buildPaymentRequest());
	}

	private PaymentRequest buildPaymentRequest() {
		PaymentRequest pr = new PaymentRequest();
		pr.setAppId("money-payment");
		pr.setTradeNo(String.valueOf(System.currentTimeMillis()));
		pr.setPaymentOrderInfo(buildPaymentOrderInfo());
		pr.setPaymentPartyInfo(buildPaymentParty());
		return pr;
	}
   private PaymentPartyInfo buildPaymentParty() {
		PaymentPartyInfo pp = new PaymentPartyInfo();
		pp.setPayer(PartyInfo.instance("3103001103583100096753664", "1908817000118536"));
		pp.setPayee(PartyInfo.instance("3103001103539592325509120", "1908817000118535"));
		pp.setBizInitiator(pp.getPayer());
		return pp;
	}

	private PaymentOrderInfo buildPaymentOrderInfo() {
		PaymentOrderInfo pi = new PaymentOrderInfo();
		pi.setGoodsName("goodsName");
		pi.setPayRemark("test_pay");
		pi.setPayAmount(BigDecimal.valueOf(1));
		return pi;
	}
   ```
   
   ### 转账
   ```java
   	@Reference(version = "1.0.0", check = false, timeout = 999999999, retries = -1)
	private IPaymentService paymentService;

	@Test
	public void trade() throws Exception {
		PaymentResult rep = this.paymentService.payment(buildPaymentRequest());
		System.out.println(rep);
	}

	private PaymentRequest buildPaymentRequest() {
		String bizNo = "";
		bizNo = String.valueOf(System.currentTimeMillis());
		PaymentRequest pr = new PaymentRequest();
		pr.setAppId("money-payment");
		pr.setTradeNo(bizNo);
		pr.setPaymentOrderInfo(buildPaymentOrderInfo());
		pr.setPaymentPartyInfo(buildPaymentParty());
		return pr;
	}

	private PaymentPartyInfo buildPaymentParty() {
		PaymentPartyInfo pp = new PaymentPartyInfo();
		pp.setPayer(PartyInfo.instance("3103001103583100096753664", "1908817000118536"));
		pp.setPayee(PartyInfo.instance("3103001103539592325509120", "1908817000118535"));
		pp.setBizInitiator(pp.getPayer());
		return pp;
	}

	private PaymentOrderInfo buildPaymentOrderInfo() {
		PaymentOrderInfo pi = new PaymentOrderInfo();
//		pi.setCouponsAmount(BigDecimal.valueOf(22));
//		pi.setIndividualAmount(BigDecimal.valueOf(18));
//		pi.setUnionAmount(BigDecimal.valueOf(10));
		pi.setGoodsName("goodsName");
		pi.setPayRemark("test_pay");
		pi.setNotifyurl("http://www.baidu.com/");
		//pi.setPayAmount(pi.getUnionAmount().add(pi.getIndividualAmount()).add(pi.getCouponsAmount()));
		pi.setPayAmount(BigDecimal.valueOf(1));
		return pi;
	}
   ```
 
## channel
   外部资金渠道接入，不同渠道根据渠道编号进行路由。
