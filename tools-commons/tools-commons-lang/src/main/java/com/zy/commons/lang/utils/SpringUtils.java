package com.zy.commons.lang.utils;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import java.util.Objects;

public class SpringUtils implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (Objects.isNull(SpringUtils.applicationContext)) {
            SpringUtils.applicationContext = applicationContext;
        }
    }

    private static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static Object getBean(String beanName)  throws BeansException{
        return getApplicationContext().getBean(beanName);
    }

    public static <T> T getBean(Class<T> clazz)  throws BeansException{
        return getApplicationContext().getBean(clazz);
    }

    public static <T> T getBean(String beanName, Class<T> clazz)  throws BeansException{
        return getApplicationContext().getBean(beanName, clazz);
    }

    public static boolean containsBean(String name)  throws BeansException{
        return getApplicationContext().containsBean(name);
    }

    public static boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
        return getApplicationContext().isSingleton(name);
    }

    public static Class<?> getType(String name) throws NoSuchBeanDefinitionException {
        return getApplicationContext().getType(name);
    }

    public static String[] getAliases(String name) throws NoSuchBeanDefinitionException {
        return getApplicationContext().getAliases(name);
    }
}
