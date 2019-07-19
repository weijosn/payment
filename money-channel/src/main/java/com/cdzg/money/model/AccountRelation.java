package com.cdzg.money.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;


/**
 * 内部账户与外部账户关联表
 *
 * @author moon.l
 * @email liaoyue11@vip.qq.com
 * @date 2019-03-29 13:55:08
 * Copyright @ 2018 成都职工 labor Co. Ltd.
 * All right reserved.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountRelation implements Serializable {

    /**
     * 序列化ID
     */
    private static final long serialVersionUID = -5809782578272943999L;

    /**
     * 主键
     */
    private String pkAccountRelation;

    /**
     * 内部账户
     */
    private String accountNo;

    /**
     * 关联账户
     */
    private String relationNo;

    /**
     * 关联账户类型 1：通联，2：中金
     */
    private Integer relationType;

    /**
     * 开户描述
     */
    private String desc;

    /**
     * 创建时间
     */
    private Date createTime;

}
