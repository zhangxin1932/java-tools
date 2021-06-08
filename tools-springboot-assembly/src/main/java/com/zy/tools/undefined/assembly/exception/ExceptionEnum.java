package com.zy.tools.undefined.assembly.exception;

import com.zy.commons.lang.exception.ErrorInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ExceptionEnum implements ErrorInfo {
    ERR_0001("0001", "params error, id must bigger than 123"),
    ERR_0002("0002", "unknown error"),
    ;

    private String code;
    private String msg;

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMsg() {
        return msg;
    }
}
