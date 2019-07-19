package com.cdzg.money.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.cdzg.money.mapper.entity.NotifyPO;

@Mapper
public interface NotifyMapper {

	NotifyPO findByOrderId(String orderNo);

	List<NotifyPO> queryPending();

	int updateToPending(@Param("id")Long id);

	void updateNotify(@Param("id")Long id,@Param("status") int status,@Param("notifyTimes") int i,@Param("notifyTime") Date time);

	void save(NotifyPO notify);

}
