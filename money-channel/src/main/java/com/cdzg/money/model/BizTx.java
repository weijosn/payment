package com.cdzg.money.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: 订单号与交易流水号关联表
 * @Author : pc.huang
 * @Date : 2019-04-02 13:51
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BizTx implements Serializable {
    private static final long serialVersionUID = 8220458187027208743L;

    /**
     * 主键
     */
    private String pkBizTx;

    /**
     * 订单号
     */
    private String bizNo;

    /**
     * 交易流水号
     */
    private String txSn;

    /**
     * 机构类型
     */
    private String instCode;

    /**
     * 支付类型 0：钱包，1:三类户
     */
    private int grantType;

    /**
     * pkorg
     */
    private String pkOrg;

    /**
     * 创建时间
     */
    private Date creatTime;
}
