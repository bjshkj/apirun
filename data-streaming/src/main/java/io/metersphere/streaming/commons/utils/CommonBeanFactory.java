package io.metersphere.streaming.commons.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class CommonBeanFactory implements ApplicationContextAware {
    private static ApplicationContext context;

    public CommonBeanFactory() {
    }

    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        context = ctx;
    }

    public static Object getBean(String beanName) {
        return context != null && !StringUtils.isBlank(beanName) ? context.getBean(beanName) : null;
    }

    public static <T> T getBean(Class<T> className) {
        return context != null && className != null ? context.getBean(className) : null;
    }
}

