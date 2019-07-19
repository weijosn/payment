package com.cdzg.money.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.cdzg.money.mapper.entity.ClusterLockPO;

@Mapper
public interface ClusterLockMapper {

	@Update("update tb_cluster_lock set lock_status = 1,lock_time=sysdate() where task_name = #{taskName} and lock_status = 0 ")
	public int lockTask(@Param("taskName") String taskName);

	@Select("select * from tb_cluster_lock where task_name = #{taskName}")
	public ClusterLockPO getLockTask(@Param("taskName") String taskName);

	@Insert("insert into tb_cluster_lock(task_name,lock_status)values(#{taskName},0)")
	public void insertTask(@Param("taskName") String taskName);

	@Update("update tb_cluster_lock set lock_status = 0 where task_name = #{taskName}")
	public void unlock(@Param("taskName") String taskName);

}