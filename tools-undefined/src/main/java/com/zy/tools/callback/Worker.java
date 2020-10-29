package com.zy.tools.callback;

/**
 * 一个worker，它需要有个方法，来代表这个worker将来做什么，action就可以理解为一个耗时任务。action可以接收一个参数。
 */
public interface Worker {
    String action(Object object);
}
