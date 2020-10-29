package com.zy.tools.retry.v2;

import lombok.AllArgsConstructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 基于 jdk 动态代理实现重试功能, 适用于有接口的业务
 */
@AllArgsConstructor
public class JdkProxy implements InvocationHandler {
    private final Object target;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        int times = 0;
        while (times < 3) {
            try {
                return method.invoke(target, args);
            } catch (Throwable e) {
                times ++;
                System.out.println("times >>>>>>>>> " + times);
                if (times >= 3) {
                    throw new RuntimeException(e);
                }
            }
        }
        return null;
    }

    /**
     * 获取动态代理对象
     * @param realObj 真实对象
     * @return
     */
    public static Object getProxy(Object realObj) {
        InvocationHandler handler = new JdkProxy(realObj);
        return Proxy.newProxyInstance(handler.getClass().getClassLoader(), realObj.getClass().getInterfaces(), handler);
    }
}

