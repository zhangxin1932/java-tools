package com.zy.commons.lang;

import com.zy.commons.lang.exception.ErrorInfo;
import com.zy.commons.lang.validator.Validators;
import org.junit.Test;

public class ValidatorsTest {
    @Test
    public void fn01() {
        Validators.ifBlank("").thenThrow(new ErrorInfo() {
            @Override
            public String getCode() {
                return "500";
            }

            @Override
            public String getMsg() {
                return "参数错误";
            }
        });
    }
}
