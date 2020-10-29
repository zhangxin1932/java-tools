package com.zy.commons.lang.inject;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public final class InjectedBeans {

    private static Map<String, Object> BEAN_MAP = new ConcurrentHashMap<>();

    public static  <T> T getSingletonBean(Class<T> clazz) {
        if (Objects.isNull(clazz)) {
            return null;
        }
        String beanName = generateBeanName(clazz);
        Object bean = BEAN_MAP.get(beanName);
        if (Objects.isNull(bean)) {
            createSingletonBean(beanName, clazz);
            bean = BEAN_MAP.get(beanName);
        }
        if (clazz.isAssignableFrom(bean.getClass())) {
            return clazz.cast(bean);
        }
        return null;
    }

    public static void createSingletonBean(Class<?> clazz) {
        createSingletonBean(generateBeanName(clazz), clazz);
    }

    public static void createSingletonBean(String beanName, Class<?> clazz) {
        if (StringUtils.isBlank(beanName) || Objects.isNull(clazz)) {
            throw new IllegalArgumentException("failed to create bean, beanName or clazz is null.");
        }
        if (Objects.nonNull(BEAN_MAP.get(beanName))) {
            log.warn("bean {} had already in bean map.", beanName);
            return;
        }
        try {
            Object obj = clazz.newInstance();
            BEAN_MAP.put(beanName, obj);
        } catch (Exception e) {
            log.error("failed to create bean {}.", beanName);
            throw new RuntimeException(e);
        }
    }

    public static String generateBeanName(Class<?> clazz) {
        if (Objects.isNull(clazz)) {
            throw new IllegalArgumentException("failed to generate beanName.");
        }
        return clazz.getTypeName();
    }

}
