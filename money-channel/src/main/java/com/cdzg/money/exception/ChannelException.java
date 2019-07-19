package com.cdzg.money.exception;


import com.cdzg.money.api.channel.common.ChannelResultCode;
import lombok.Getter;

/**
 * @author appple
 */
@Getter
public class ChannelException extends RuntimeException {

    private static final long serialVersionUID = 1857440708804128584L;
    /**
     * 错误码
     */
    private String code;
    /**
     * 提示消息
     */
    private String msg;

    public ChannelException(ChannelResultCode resultCode) {
        this.code = resultCode.getCode();
        this.msg = resultCode.getMessage();
    }
}
