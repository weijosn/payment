package com.cdzg.money.service.module.channel;

import org.apache.commons.lang3.StringUtils;

import com.cdzg.money.api.payment.request.Extension;

import lombok.Getter;
import lombok.Setter;

/**
 * 结算协议
 * 
 * @author jiangwei
 *
 */
public interface ChannelSettleProtocol {

	StageResult doSettle(long id);

	@Getter
	@Setter
	class StageResult {
		StageResultEnum result;
		Extension extension;

		public static StageResult instance(StageResultEnum result,Extension extension) {
			StageResult sr = new StageResult();
			sr.result = result;
			sr.extension = extension;
			return sr;
		}
		public boolean next() {
			return result == StageResultEnum.SUCCESS;
		}
	}

	enum StageResultEnum {

		WAIT("W", "付款中"),

		SUCCESS("PS", "支付成功"),

		FAILED("F", "支付失败");

		private final String code;
		private final String desc;

		StageResultEnum(String code, String desc) {
			this.code = code;
			this.desc = desc;

		}

		public String getCode() {
			return code;
		}

		public String getDesc() {
			return desc;
		}

		public static StageResultEnum getByCode(String code) {
			for (StageResultEnum type : StageResultEnum.values()) {
				if (StringUtils.equals(code, type.getCode())) {
					return type;
				}
			}
			return null;
		}
	}

}
