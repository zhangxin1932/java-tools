package com.zy.tools.retry.v3;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import java.lang.reflect.Method;

public class CglibProxy implements MethodInterceptor {
    @Override
    public Object intercept(Object object, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        int times = 0;
        while (times < 3) {
            try {
                //通过代理子类调用父类的方法
                return methodProxy.invokeSuper(object, args);
            } catch (Throwable e) {
                times++;
                System.out.println("times >>>>>>>>> " + times);
                if (times >= 3) {
                    throw new RuntimeException(e);
                }
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> tClass) {
        return (T) Enhancer.create(tClass, this);
    }
}

