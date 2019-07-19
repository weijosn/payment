package com.cdzg.money.api.channel;

import com.cdzg.money.api.channel.common.ChannelResultCode;
import lombok.*;

import java.io.Serializable;

/**
 * 基本响应对象
 *
 * @author appple
 */
@Getter
@Setter
@ToString()
public class BaseResponse implements Serializable {
    private static final long serialVersionUID = 3209576079479957410L;
    /**
     * 响应码
     */
    private String resultCode;

    /**
     * 响应消息
     */
    private String resultMessage;

    /**
     * 响应是否成功
     *
     * @return
     */
    public boolean isSuccess() {
        return ChannelResultCode.SUCCED.getCode().equals(resultCode);
    }

}
