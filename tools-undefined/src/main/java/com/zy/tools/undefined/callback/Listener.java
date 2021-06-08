package com.zy.tools.undefined.callback;

/**
 * 这个listener用来做为回调，将worker的执行结果，放到result的参数里。
 */
public interface Listener {
    void result(Object result);
}
