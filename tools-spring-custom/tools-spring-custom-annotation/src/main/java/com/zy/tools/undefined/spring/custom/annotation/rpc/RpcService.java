package com.zy.tools.undefined.spring.custom.annotation.rpc;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface RpcService {
}
