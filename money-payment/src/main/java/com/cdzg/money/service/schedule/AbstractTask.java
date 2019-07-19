package com.cdzg.money.service.schedule;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.cdzg.money.service.notify.ClusterLockBiz;

public class AbstractTask implements InitializingBean {

	@Autowired
	protected ClusterLockBiz clusterLockbiz;

	@Override
	public void afterPropertiesSet() throws Exception {
		clusterLockbiz.newTask(this.getClass().getName());
	}

	protected boolean lock() {
		return clusterLockbiz.lock(this.getClass().getName());
	}

	protected void unlock() {
		clusterLockbiz.unlock(this.getClass().getName());
	}

}