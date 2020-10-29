package com.zy.commons.lang.exception;

import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.xml.bind.ValidationException;
import java.text.MessageFormat;
import java.util.Locale;

@Order(Ordered.HIGHEST_PRECEDENCE + 5)
public interface ExceptionAdvice {

    ServiceExceptionFactory getServiceExceptionFactory();

    MessageSource getMessageSource();

    default ServiceException wrapServiceException(Throwable e) {
        return createServiceException(parseServiceException(e));
    }

    default ServiceException createServiceException(ServiceException e) {
        String code = getCode(e);
        String msg = getMsg(e);
        ServiceException exception = getServiceExceptionFactory().create(code, msg);
        exception.setStackTrace(e.getStackTrace());
        return exception;
    }

    default ServiceException parseServiceException(Throwable e) {
        ServiceException exception;
        if (e instanceof ServiceException) {
            exception = (ServiceException) e;
        } else if (e instanceof ValidationException || e instanceof BindException || e instanceof MethodArgumentNotValidException || e instanceof MethodArgumentTypeMismatchException) {
            exception = ServiceException.INVALID_PARAMS_ERROR;
        } else {
            exception = ServiceException.UNKNOWN_ERROR;
        }
        return exception;
    }

    default String getMsg(ServiceException e) {
        boolean i18nEnabled = getServiceExceptionFactory().i18nEnabled();
        if (i18nEnabled) {
            try {
                return getMessageSource().getMessage(e.getMsg(), e.getArgs(), Locale.CHINA);
            } catch (NoSuchMessageException ex) {
                return e.getMsg();
            }
        } else {
            return MessageFormat.format(e.getMessage(), e.getArgs());
        }
    }

    default String getCode(ServiceException e) {
        boolean i18nEnabled = getServiceExceptionFactory().i18nEnabled();
        if (i18nEnabled) {
            try {
                return getMessageSource().getMessage(e.getMsg(), null, Locale.ROOT);
            } catch (NoSuchMessageException ex) {
                return e.getCode();
            }
        } else {
            return e.getCode();
        }
    }
}
