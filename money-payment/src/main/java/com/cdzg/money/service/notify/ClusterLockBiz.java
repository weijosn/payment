package com.cdzg.money.service.notify;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cdzg.money.mapper.ClusterLockMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ClusterLockBiz {

	@Autowired
	private ClusterLockMapper clusterLock;

	private List<String> unlockList = new CopyOnWriteArrayList<String>();

	public void newTask(String name) {
		if (this.clusterLock.getLockTask(name) == null) {
			this.clusterLock.insertTask(name);
		}
	}

	public void unlock(String name) {
		try {
			log.info("un lock task {}", name);
			clusterLock.unlock(name);
		} catch (Exception e) {
			log.info("unlock error , {} , task name {}", e.getMessage(), name);
			unlockList.add(name);
		}

	}

	public boolean lock(String name) {
		log.info("start lock task {}", name);
		return clusterLock.lockTask(name) == 1;
	}

	public void clearLock() {
		String keys[] = unlockList.stream().toArray(String[]::new);
		for (String key : keys) {
			unlockList.remove(key);
			unlock(key);
		}
	}
}