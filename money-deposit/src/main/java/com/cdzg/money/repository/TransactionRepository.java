package com.cdzg.money.repository;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cdzg.money.mapper.TransactionMapper;
import com.cdzg.money.mapper.entity.TransactionPO;
import com.cdzg.money.model.Transaction;
import com.cdzg.money.model.enums.TransactionStatusEnum;

@Component
public class TransactionRepository {

	@Autowired
	private TransactionMapper transactionMapper;

	public void save(String bizNo, String appId) {
		TransactionPO bean = new TransactionPO();
		bean.setAppId(appId);
		bean.setBizNo(bizNo);
		bean.setInsertTime(new Date());
		bean.setStatus(TransactionStatusEnum.Pending.getCode());
		transactionMapper.save(bean);
	}

	public Transaction getTransaction(String bizNo, String appId) {
		return toTransaction(transactionMapper.findByPrimaryKey(bizNo, appId));
	}

	private Transaction toTransaction(TransactionPO bo) {
		if (bo == null)
			return null;
		Transaction tr = new Transaction();
		tr.setAppid(bo.getAppId());
		tr.setBizno(bo.getBizNo());
		tr.setStatus(TransactionStatusEnum.getByCode(bo.getStatus()));
		return tr;
	}

	// 提交账务为有效
	public int commit(String requestNo, String appId) {
		return this.transactionMapper.updateToSucced(requestNo, appId);
	}

	// 提交账务为无效
	public boolean invalid(String requestNo, String appId) {
		Transaction transaction = this.getTransaction(requestNo, appId);
		if (transaction == null) {
			return true ;
		}
		// 判断是否在进行中
		if (transaction.getStatus() == TransactionStatusEnum.Pending) {
			return 1 == transactionMapper.updateToStatus(requestNo, appId, TransactionStatusEnum.Invalid.getCode(),
					TransactionStatusEnum.Pending.getCode());
		}
		
		return false;
	}

}
