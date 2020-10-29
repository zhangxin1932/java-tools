package com.zy.commons.lang.response;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import java.io.Serializable;
import java.util.Objects;

@Getter
@ToString
@EqualsAndHashCode
public class ResponseVO<T> implements Serializable {
    private static final ResponseVO<?> EMPTY = new ResponseVO<>();
    private static final long serialVersionUID = 7671954999359369622L;
    private final Boolean success;
    private final String msg;
    private final String code;
    private final T data;

    public static <T> ResponseVO<T> of(T response) {
        if (Objects.isNull(response)) {
            return empty();
        }
        return new ResponseVO<>(response);
    }

    public static <T> ResponseVO<T> error(String code, String msg, T response) {
        return new ResponseVO<>(false, code, msg, response);
    }

    public static <T> ResponseVO<T> error(T response) {
        return error(null, null, response);
    }

    public static <T> ResponseVO<T> error(String code, String msg) {
        return error(code, msg, null);
    }

    @SuppressWarnings("unchecked")
    public static <T> ResponseVO<T> empty() {
        return (ResponseVO<T>) EMPTY;
    }

    public static <T> ResponseVO<T> success() {
        return empty();
    }

    private ResponseVO() {
        this(null);
    }

    private ResponseVO(T data) {
        this(true, null, null, data);
    }

    private ResponseVO(String code, String msg) {
        this(false, code, msg, null);
    }

    private ResponseVO(Boolean success, String code, String msg, T data) {
        this.success = success;
        this.code = code;
        this.msg = msg;
        this.data = data;
        if (Objects.nonNull(this.data) && !(this.data instanceof Serializable)) {
            throw new RuntimeException("data must implements Serializable");
        }
    }
}
