package com.zy.commons.lang.validator;

import com.zy.commons.lang.exception.ErrorInfo;
import com.zy.commons.lang.exception.ServiceException;
import com.zy.commons.lang.exception.ServiceExceptionFactory;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

public abstract class Validators {
    private static ServiceExceptionFactory serviceExceptionFactory;
    private Validators () {
        throw new UnsupportedOperationException("Validators cannot be Instantiated");
    }

    /**
     * 某个对象是否为 null
     * 若判断 not null, 则在此结果前加 ! (非) 即可 -->  !ifNull(data); 下述判断类此处, 不再赘述
     * @param data
     * @param <T>
     * @return
     */
    public static <T> ValidatorCallback<T> ifNull(final T data) {
        return ifInvalid(data, Objects.isNull(data));
    }

    public static <T> ValidatorCallback<String> ifBlank(final String data) {
        return ifInvalid(data, StringUtils.isBlank(data));
    }

    public static <T> ValidatorCallback<String> ifEmpty(final String data) {
        return ifInvalid(data, StringUtils.isEmpty(data));
    }

    public static <T> ValidatorCallback<Object[]> ifAnyNull(final Object... data) {
        boolean isInvalid = false;
        for (Object obj : data) {
            if (Objects.isNull(obj)) {
                isInvalid = true;
                break;
            }
        }
        return ifInvalid(data, isInvalid);
    }

    public static <T> ValidatorCallback<String[]> ifAnyBlank(final String... data) {
        return ifInvalid(data, StringUtils.isAnyBlank(data));
    }

    public static <T> ValidatorCallback<String[]> ifAnyEmpty(final String... data) {
        return ifInvalid(data, StringUtils.isAnyEmpty(data));
    }

    public static <T extends Collection> ValidatorCallback<T> ifEmpty(final T collection) {
        return ifInvalid(collection, CollectionUtils.isEmpty(collection));
    }

    public static <K, V> ValidatorCallback<Map<K, V>> ifEmpty(final Map<K, V> map) {
        return ifInvalid(map, MapUtils.isEmpty(map));
    }

    public static <T> ValidatorCallback<T> ifInvalid(boolean isInvalid) {
        return new DefaultValidatorCallback<>(null, isInvalid);
    }

    public static <T> ValidatorCallback<T> ifInvalid(T data, boolean isInvalid) {
        return new DefaultValidatorCallback<>(data, isInvalid);
    }

    public static void throwAnyway(ErrorInfo errorInfo, Object... args) {
        throw createException(errorInfo, args);
    }

    public static void setServiceExceptionFactory(ServiceExceptionFactory serviceExceptionFactory) {
        if (Objects.isNull(Validators.serviceExceptionFactory)) {
            Validators.serviceExceptionFactory = serviceExceptionFactory;
        }
    }

    public static ServiceException createException(ErrorInfo errorInfo, Object... args) {
        if (Objects.nonNull(serviceExceptionFactory)) {
            return serviceExceptionFactory.create(errorInfo, args);
        }
        return new ServiceException(errorInfo.getCode(), errorInfo.getMsg(), args);
    }

    private static class DefaultValidatorCallback<T> implements ValidatorCallback<T> {
        private final T data;
        private final boolean isInvalid;

        DefaultValidatorCallback(T data, boolean isInvalid) {
            this.data = data;
            this.isInvalid = isInvalid;
        }

        @Override
        public T thenGet(Fallback<T> fallback) {
            if (isInvalid) {
                return fallback.get();
            }
            return data;
        }

        @Override
        public T orThrow(ErrorInfo errorInfo, Object... args) {
            thenThrow(errorInfo, args);
            return data;
        }

        @Override
        public void thenThrow(ErrorInfo errorInfo, Object... args) {
            if (isInvalid) {
                throw createException(errorInfo, args);
            }
        }

        @Override
        public void thenHandle(Handler handler) {
            if (isInvalid) {
                handler.handle();
            }
        }
    }
}
