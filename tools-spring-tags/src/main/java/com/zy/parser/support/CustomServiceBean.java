package com.zy.parser.support;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

public class CustomServiceBean implements ApplicationContextAware, ApplicationListener {
    /**
     * 接口名称 key
     */
    @Getter
    @Setter
    private String interfaceName;
    /**
     * 服务类bean value
     */
    @Getter
    @Setter
    private String ref;
    /**
     * 拦截器类
     */
    @Getter
    @Setter
    private String filterRef;

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        // if (StringUtils.isEmpty(filterRef) || Objects.equals("null", filterRef) || !(applicationContext.getBean(filterRef) instanceof Filter))
        System.out.println("...................");
    }
}
