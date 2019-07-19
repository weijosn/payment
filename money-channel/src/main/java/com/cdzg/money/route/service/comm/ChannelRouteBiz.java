package com.cdzg.money.route.service.comm;

import com.cdzg.money.api.channel.common.ChannelResultCode;
import com.cdzg.money.exception.ChannelException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @Description: 渠道路由获取bean对象服务
 * @Author : pc.huang
 * @Date : 2019-04-01 13:18
 */
@Component
public class ChannelRouteBiz implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    public ChannelProtocol route(String beanName) {
        if (StringUtils.isBlank(beanName)) {
            throw new ChannelException(ChannelResultCode.CHANNEL_NOT_FOUND_ERROR);
        }
        Object bean = null;
        try {
            bean = this.applicationContext.getBean(beanName);
        } catch (BeansException e) {
            throw new ChannelException(ChannelResultCode.GAIN_BEAN_ERROR);
        }
        if (bean == null) {
            throw new ChannelException(ChannelResultCode.BEAN_NOT_FOUND_ERROR);
        }
        return (ChannelProtocol) bean;
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
