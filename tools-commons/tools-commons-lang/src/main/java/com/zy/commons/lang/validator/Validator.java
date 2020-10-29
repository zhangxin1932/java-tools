package com.zy.commons.lang.validator;

public interface Validator<T> {
    /**
     * 验证数据是否合法
     * @param data
     * @return
     */
    boolean validate(T data);
}
