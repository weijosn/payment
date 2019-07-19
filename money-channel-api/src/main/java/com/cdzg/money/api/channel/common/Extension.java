package com.cdzg.money.api.channel.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * 扩展对象(比如用于收单、算法等内容或掊口参数没有定义的内容)
 * 
 * @author jiangwei
 *
 */


public class Extension implements Serializable {

	private static final long serialVersionUID = 1909698249499711751L;
	private List<Kvp> entryList = new ArrayList<Kvp>();

	/**
	 * Get the list of 'entry' element items.
	 * 
	 * @return list
	 */
	public List<Kvp> getEntries() {
		return entryList;
	}

	/**
	 * 添加一个属性
	 * 
	 * @param kvp
	 */
	public void addExtProperty(Kvp kvp) {
		if (entryList == null) {
			entryList = new ArrayList<Kvp>();
		}
		entryList.add(kvp);
	}

	@Getter
	@Setter
	public static class Kvp implements Serializable {
		private static final long serialVersionUID = -4124073547479307642L;
		private String key;
		private String value;

		public static Kvp instance(String key, String value) {
			Kvp kvp = new Kvp();
			kvp.setKey(key);
			kvp.setValue(value);
			return kvp;
		}
	}

}
