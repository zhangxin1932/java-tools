package com.zy.commons.lang.exception;

import com.zy.commons.lang.validator.Validators;
import org.springframework.beans.factory.InitializingBean;

public class DefaultServiceExceptionFactory implements ServiceExceptionFactory, InitializingBean {
    @Override
    public ServiceException create(String code, String msg) {
        return new ServiceException(code, msg);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Validators.setServiceExceptionFactory(this);
    }
}
