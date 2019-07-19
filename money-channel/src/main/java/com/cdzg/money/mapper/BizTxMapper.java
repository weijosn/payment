package com.cdzg.money.mapper;

import com.cdzg.money.model.BizTx;
import org.springframework.stereotype.Repository;

/**
 * @Description: 订单号与交易流水号mapper
 * @Author : pc.huang
 * @Date : 2019-04-02 13:55
 */
@Repository
public interface BizTxMapper {


    /**
     * 保存数据
     *
     * @param bizTx
     */
    void save(BizTx bizTx);


    /**
     * 根据订单号查询数据
     *
     * @return
     */
    BizTx queryObject(String bizNo);
}
