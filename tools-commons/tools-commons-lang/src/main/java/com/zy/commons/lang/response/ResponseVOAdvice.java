package com.zy.commons.lang.response;

import com.zy.commons.lang.exception.ExceptionAdvice;
import com.zy.commons.lang.exception.ServiceException;
import com.zy.commons.lang.exception.ServiceExceptionFactory;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.Objects;

@RestControllerAdvice
public class ResponseVOAdvice implements ExceptionAdvice {
    private final MessageSource messageSource;
    private ServiceExceptionFactory serviceExceptionFactory;

    public ResponseVOAdvice(MessageSource messageSource, ServiceExceptionFactory serviceExceptionFactory) {
        this.messageSource = messageSource;
        this.serviceExceptionFactory = serviceExceptionFactory;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Object handleValidException(MethodArgumentNotValidException e){
        // fixme 这里打印出堆栈信息
        e.printStackTrace();
        if (Objects.nonNull(e.getBindingResult().getFieldError())) {
            return ResponseVO.error(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()), e.getBindingResult().getFieldError().getDefaultMessage());
        }
        // fixme 兜底
        ServiceException serviceException = wrapServiceException(e);
        return ResponseVO.error(serviceException.getCode(), serviceException.getMessage());
    }

    @ExceptionHandler(Throwable.class)
    public Object handleException(Throwable e) {
        // fixme 这里打印出堆栈信息
        e.printStackTrace();
        ServiceException serviceException = wrapServiceException(e);
        return ResponseVO.error(serviceException.getCode(), serviceException.getMessage());
    }

    public void setServiceExceptionFactory(ServiceExceptionFactory serviceExceptionFactory) {
        if (Objects.nonNull(serviceExceptionFactory)) {
            this.serviceExceptionFactory = serviceExceptionFactory;
        }
    }

    @Override
    public ServiceExceptionFactory getServiceExceptionFactory() {
        return serviceExceptionFactory;
    }

    @Override
    public MessageSource getMessageSource() {
        return messageSource;
    }
}
