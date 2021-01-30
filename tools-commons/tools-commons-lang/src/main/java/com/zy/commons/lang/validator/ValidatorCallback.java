package com.zy.commons.lang.validator;

import com.zy.commons.lang.exception.ErrorInfo;

import java.util.function.Supplier;

public interface ValidatorCallback<T> {
    /**
     * 降级处理, 如果待验证的数据是空值的话, 返回默认值
     *
     * @param fallback
     * @return
     */
    T thenGet(Supplier<T> fallback);

    /**
     * 如果待验证数据非法, 则抛出异常
     *
     * @param errorInfo
     * @param args
     * @return
     */
    T orThrow(ErrorInfo errorInfo, Object... args);

    /**
     *
     * @param errorInfo
     * @param args
     */
    void thenThrow(ErrorInfo errorInfo, Object... args);

    /**
     * 如果待验证的数据是空值, 进行后续逻辑处理
     * @param handler
     */
    void thenHandle(ValidatorCallback.Handler handler);

    /**
     * 空值处理器
     */
    interface Handler {
        /**
         * 空值处理回调函数
         */
        void handle();
    }
}
