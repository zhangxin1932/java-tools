package com.zy.commons.lang.exception;

public class ServiceException extends RuntimeException {
    private static final long serialVersionUID = 6389574757388983703L;
    public static final Object[] DEFAULT_ARGS = {};
    public static final ServiceException UNKNOWN_ERROR = new ServiceException("590", DEFAULT_ARGS);
    public static final ServiceException INVALID_PARAMS_ERROR = new ServiceException("400", "invalid params error");

    private final String msg;
    private final String code;
    private final transient Object[] args;

    public ServiceException(String msg) {
        this(msg, DEFAULT_ARGS);
    }

    public ServiceException(String msg, Object[] args) {
        super(msg);
        this.msg = msg;
        this.args = args;
        this.code = "";
    }

    public ServiceException(String code, String message) {
        this(code, message, message);
    }

    public ServiceException(String code, String message, Object[] args) {
        super(message);
        this.msg = message;
        this.code = code;
        this.args = args;
    }

    public ServiceException(String code, String msg, String message) {
        super(message);
        this.msg = msg;
        this.code = code;
        this.args = DEFAULT_ARGS;
    }

    public String getMsg() {
        return msg;
    }

    public String getCode() {
        return code;
    }

    public Object[] getArgs() {
        return args;
    }
}
