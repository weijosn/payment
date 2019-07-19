package com.cdzg.money.service.module;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cdzg.money.mapper.NextvalMapper;
import com.cdzg.money.model.enums.NextvalTypeEnum;

@Component
public class NextvalBiz {

	@Autowired
	private NextvalMapper nextvalMapper;

	public String nextval(NextvalTypeEnum order) {
		return order.wrap(this.nextvalMapper.nextval(order.getCode()));
	}

}
