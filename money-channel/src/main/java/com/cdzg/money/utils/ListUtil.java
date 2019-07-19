package com.cdzg.money.utils;

import java.util.List;

/**
 * @Description: 集合工具类
 * @Author : pc.huang
 * @Date : 2019-04-02 13:04
 */
public class ListUtil {

    /**
     * 判断集合不为空
     *
     * @param list
     * @return
     */
    public static boolean isNotNull(List list) {
        if (null != list && list.size() > 0) {
            return true;
        }
        return false;
    }
}
