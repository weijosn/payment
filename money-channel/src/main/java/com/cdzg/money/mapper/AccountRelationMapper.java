package com.cdzg.money.mapper;

import com.cdzg.money.api.channel.request.OpenAccountRequest;
import com.cdzg.money.model.AccountRelation;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 内部账户与外部账户关联表
 *
 * @author moon.l
 * @email liaoyue11@vip.qq.com
 * @date 2019-03-29 13:55:08
 * Copyright @ 2018 成都职工 labor Co. Ltd.
 * All right reserved.
 */
@Repository
public interface AccountRelationMapper {

    /**
     * 查询单条数据
     *
     * @param id
     * @return
     */
    AccountRelation queryObject(Object id);

    /**
     * 根据内部账户查询
     *
     * @param accountNo
     * @return
     */
    AccountRelation queryByAccountNo(String accountNo);

    /**
     * 根据条件查询
     *
     * @param open
     * @return
     */
    AccountRelation queryByOpenAccount(@Param("open") OpenAccountRequest open);

    /**
     * 添加内部账户与外部账户关联表数据
     *
     * @param accountRelation
     */
    void save(AccountRelation accountRelation);

    /**
     * 内部账户与外部账户关联表数据修改
     *
     * @param accountRelation
     * @return
     */
    int update(AccountRelation accountRelation);

    /**
     * 内部账户与外部账户关联表数据删除
     *
     * @param id
     * @return
     */
    int delete(Object id);

    /**
     * 批量的内部账户与外部账户关联表数据删除
     *
     * @param id
     * @return
     */
    int deleteBatch(Object[] id);

    /**
     * 分页查询内部账户与外部账户关联表数据
     *
     * @param map
     * @return
     */
    List<AccountRelation> queryList(Map<String, Object> map);

    /**
     * 数据内部账户与外部账户关联表条数查询
     *
     * @param map
     * @return
     */
    int queryTotal(Map<String, Object> map);

    /**
     * 查询全部的数据
     *
     * @param map
     * @return
     */
    List<AccountRelation> queryData(Map<String, Object> map);


}
