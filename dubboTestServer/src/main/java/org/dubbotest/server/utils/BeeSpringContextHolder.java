package org.dubbotest.server.utils;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class BeeSpringContextHolder implements ApplicationContextAware {
    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext context) {
        BeeSpringContextHolder.context = context;
    }

    public static <T> T getBean(Class<T> beanClass) {
        return context.getBean(beanClass);
    }

    public static Object getBean(String name) {
        return context.getBean(name);
    }
}
