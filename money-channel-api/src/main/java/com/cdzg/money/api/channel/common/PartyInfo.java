package com.cdzg.money.api.channel.common;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 参与方信息
 *
 * @author appple
 */
@Getter
@Setter
public class PartyInfo implements Serializable {

    private static final long serialVersionUID = -9192844446680931328L;

    /**
     * 储值账户: 内部系统账户
     */
    private String accountNo;

    /**
     * uid/shopId
     */
    private String memberId;

    public static PartyInfo instance(String accountNo, String memberId) {
        PartyInfo pi = new PartyInfo();
        pi.accountNo = accountNo;
        pi.memberId = memberId;
        return pi;
    }

}
