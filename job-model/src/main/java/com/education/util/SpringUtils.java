package com.education.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @Author: Jet
 * @Description: 获取 spring 中注入的 bean 实例
 * @Date: 2018/3/29 14:13
 */
@Component
public class SpringUtils implements ApplicationContextAware {

    private static ApplicationContext CONTEXT;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        CONTEXT = applicationContext;
    }

    /**
     * Get a Spring bean by type.
     **/
    public static <T> T getBean(Class<T> beanClass) {
        if (beanClass == null) return null;
        try {
            return CONTEXT.getBean(beanClass);
        } catch (BeansException e) {
            return null;
        }
    }

    /**
     * Get a Spring bean by name.
     **/
    public static Object getBean(String beanName) {
        if (StringUtils.isBlank(beanName)) return null;
        try {
            return CONTEXT.getBean(beanName);
        } catch (BeansException e) {
            return null;
        }
    }

}
