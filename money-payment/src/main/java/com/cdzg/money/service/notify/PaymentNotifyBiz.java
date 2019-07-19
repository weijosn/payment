package com.cdzg.money.service.notify;

import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.cdzg.money.init.CryptUtils;
import com.cdzg.money.mapper.NotifyMapper;
import com.cdzg.money.mapper.PaymentOrderMapper;
import com.cdzg.money.mapper.entity.NotifyPO;
import com.cdzg.money.mapper.entity.PaymentOrderPO;

import lombok.extern.slf4j.Slf4j;
import okhttp3.FormBody;
import okhttp3.FormBody.Builder;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@Component
@Slf4j
public class PaymentNotifyBiz implements Runnable, InitializingBean {

	private final ExecutorService threadPool = Executors.newCachedThreadPool();
	private final BlockingQueue<String> queue = new ArrayBlockingQueue<String>(5000);

	private OkHttpClient client = null;

	@Autowired
	private PaymentOrderMapper orderMapper;
	@Autowired
	private NotifyMapper notifyMapper;

	
	public static OkHttpClient buildHttpClient() throws Exception {
		final X509TrustManager trustManager = new X509TrustManager() {
			@Override
			public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

			}

			@Override
			public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

			}

			@Override
			public X509Certificate[] getAcceptedIssuers() {
				return new X509Certificate[0];
			}
		};

		SSLContext sslContext = SSLContext.getInstance("SSL");
		sslContext.init(null, new TrustManager[] { trustManager }, new SecureRandom());
		SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

		return new OkHttpClient.Builder().sslSocketFactory(sslSocketFactory, trustManager)
				.hostnameVerifier(new HostnameVerifier() {
					@Override
					public boolean verify(String hostname, SSLSession session) {
						return true;
					}
				}).connectTimeout(30, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).build();
	}

	public void notify(String orderNo) {
		if (queue.contains(orderNo)) {
			return;
		}
		queue.add(orderNo);
	}

	class HttpNotify implements Runnable {

		private NotifyPO notify;

		public HttpNotify(NotifyPO notify) {
			this.notify = notify;
		}

		@Override
		public void run() {

			boolean success = false;

			Map<String, String> params = buildParam(notify);

			try {
				success = post(notify.getNotifyUrl(), toParamBuilder(params));
			} catch (Exception e) {
				log.error("http error {} , url {}", e.getMessage(), notify.getNotifyUrl());
			}

			Calendar calendar = Calendar.getInstance();
			if (success) {
				notifyMapper.updateNotify(notify.getId(), NotifyPO.NOTIFY_OK, 1, calendar.getTime());
			} else {
				calendar.add(Calendar.MINUTE, notify.getNotifyTimes() * 10);
				notifyMapper.updateNotify(notify.getId(), NotifyPO.NOTIFY_ING, notify.getNotifyTimes() + 1,calendar.getTime());
			}

		}

		private Builder toParamBuilder(Map<String, String> params) {
			FormBody.Builder formBody = new FormBody.Builder();
			for (Map.Entry<String, String> param : params.entrySet()) {
				formBody.add(param.getKey(), param.getValue());
			}
			return formBody;
		}

	}

	Map<String, String> buildParam(NotifyPO notify) {

		Map<String, String> params = new HashMap<String, String>();
		PaymentOrderPO order = orderMapper.findByOrderId(notify.getPaymentOrderNo());

		StringBuilder buf = new StringBuilder(64);
		buf.append(order.getPaymentOrderId());
		buf.append("&");
		buf.append(order.getTradeNo());
		buf.append("&");
		buf.append(order.getAppId());
		buf.append("&");
		buf.append(order.getOrderAmount().toPlainString());
		buf.append("&");
		buf.append(order.getPaiedAmount().toPlainString());
		buf.append("&");
		buf.append(order.getPaymentCode());
		buf.append("&123456789");

		params.put("orderNo", order.getPaymentOrderId());
		params.put("tradeNo", order.getTradeNo());
		params.put("orderAmount", order.getOrderAmount().toPlainString());
		params.put("paiedAmount", order.getPaiedAmount().toPlainString());
		params.put("paymentCode", order.getPaymentCode());
		params.put("orderStatus", order.getOrderStatus());
		params.put("sign", CryptUtils.md5(buf.toString()));

		log.info("[notify] notify pay result {},params {}", notify.getNotifyUrl(), buf.toString());

		return params;
	}

	boolean post(String url, FormBody.Builder formBody) throws IOException {

		if (StringUtils.isEmpty(url)) {
			return true;
		}

		if (!StringUtils.startsWithIgnoreCase(url, "http://") && !StringUtils.startsWithIgnoreCase(url, "https://")) {
			return true;
		}

		Request request = new Request.Builder().url(url).post(formBody.build()).build();
		Response response = client.newCall(request).execute();

		try {
			if (response.isSuccessful()) {
				String body = response.body().string();
				return "ok".equalsIgnoreCase(body);
			} else {
				log.warn("[notify] post notify pay result failed. code {}, url {}", response.code(), url);
				return false;
			}
		} finally {
			response.close();
		}

	}

	@Override
	public void run() {
		while (true) {
			NotifyPO notify = null;
			try {
				notify = notifyMapper.findByOrderId(queue.take());
				if (notify != null && notifyMapper.updateToPending(notify.getId()) == 1) {
					threadPool.execute(new HttpNotify(notify));
				}
			} catch (Exception e) {
				if (notify != null) {
					log.error("notify error . notify {} " + notify.toString(), e);
				} else {
					log.error("notify error , {}", e.getMessage());
				}
			}
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.client = buildHttpClient();
		threadPool.execute(this);
	}

	public void resue() {
		List<NotifyPO> list = notifyMapper.queryPending();
		for (NotifyPO n : list) {
			notify(n.getPaymentOrderNo());
		}
	}

	public void doNotify(String orderNo) {
		NotifyPO notify = notifyMapper.findByOrderId(orderNo);
		Assert.isTrue(notify != null, "未填写通知地址！");
		notify(orderNo);
	}

}
