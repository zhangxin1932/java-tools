package com.zy.commons.lang.exception;

public interface ServiceExceptionFactory {
    ServiceException create(String code, String msg);

    default ServiceException create(ErrorInfo errorInfo, Object... args) {
        return new ServiceException(errorInfo.getCode(), errorInfo.getMsg(), args);
    }

    /**
     * 是否启用国际化
     *
     * @return
     */
    default boolean i18nEnabled() {
        return true;
    }
}
