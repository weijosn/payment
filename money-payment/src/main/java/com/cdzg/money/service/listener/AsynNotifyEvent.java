package com.cdzg.money.service.listener;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AsynNotifyEvent extends ApplicationEvent {

	private static final long serialVersionUID = 7183742063538509557L;

	public AsynNotifyEvent(Object source) {
		super(source);
	}

	public String toString() {
		if (source != null) {
			return source.toString();
		}
		return this.toString();
	}

}
