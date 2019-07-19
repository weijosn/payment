package com.cdzg.money.utils;

import java.util.UUID;

/**
 * @Description: uuid工具类
 * @Author : pc.huang
 * @Date : 2019-02-21 16:45
 */
public class UuidUtil {
    /**
     * 获取大写的uuid
     *
     * @return uuid
     */
    public static String getUpperId() {
        return UUID.randomUUID().toString().replace("-", "").toUpperCase();
    }

}
