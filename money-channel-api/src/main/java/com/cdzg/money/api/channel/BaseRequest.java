package com.cdzg.money.api.channel;

import lombok.Getter;
import lombok.Setter;

/**
 * 请求对象基类
 *
 * @author appple
 */
@Getter
@Setter
public abstract class BaseRequest implements java.io.Serializable {

    private static final long serialVersionUID = -2331356163493588916L;

    /**
     * appId
     */
    private String appId;

    /**
     * 订单号
     */
    private String bizNo;

}
