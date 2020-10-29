package com.zy.commons.starter;

import com.zy.commons.lang.exception.DefaultServiceExceptionFactory;
import com.zy.commons.lang.exception.ServiceExceptionFactory;
import com.zy.commons.lang.response.ResponseVOAdvice;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

@Configuration
@ConditionalOnClass(ResponseVOAdvice.class)
public class ResponseVOAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean(ServiceExceptionFactory.class)
    public ServiceExceptionFactory serviceExceptionFactory() {
        return new DefaultServiceExceptionFactory();
    }

    @Bean
    @ConditionalOnMissingBean(name = "exceptionMessageSource")
    public MessageSource exceptionMessageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setBasename("messages/exceptions");
        return messageSource;
    }

    @Bean
    @ConditionalOnMissingBean(ResponseVOAdvice.class)
    public ResponseVOAdvice responseVOAdvice(MessageSource exceptionMessageSource, ServiceExceptionFactory serviceExceptionFactory) {
        return new ResponseVOAdvice(exceptionMessageSource, serviceExceptionFactory);
    }

}
