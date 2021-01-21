package com.zy.commons.lang.concurrent;

import org.springframework.util.Assert;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;

/**
 * 经过修正后的 ScheduledThreadPoolExecutor, 防止无限制的创建线程, 因为默认的最大线程数是: Integer.MAX_VALUE
 */
public class FixedScheduledThreadPoolExecutor extends ScheduledThreadPoolExecutor {

    public FixedScheduledThreadPoolExecutor(int corePoolSize, int maxPoolSize) {
        super(corePoolSize);
        setMaxPoolSize(corePoolSize, maxPoolSize, this);
    }

    public FixedScheduledThreadPoolExecutor(int corePoolSize, ThreadFactory threadFactory, int maxPoolSize) {
        super(corePoolSize, threadFactory);
        setMaxPoolSize(corePoolSize, maxPoolSize, this);
    }

    public FixedScheduledThreadPoolExecutor(int corePoolSize, RejectedExecutionHandler handler, int maxPoolSize) {
        super(corePoolSize, handler);
        setMaxPoolSize(corePoolSize, maxPoolSize, this);
    }

    public FixedScheduledThreadPoolExecutor(int corePoolSize, ThreadFactory threadFactory, RejectedExecutionHandler handler, int maxPoolSize) {
        super(corePoolSize, threadFactory, handler);
        setMaxPoolSize(corePoolSize, maxPoolSize, this);
    }

    private void setMaxPoolSize(int corePoolSize, int maxPoolSize, ScheduledThreadPoolExecutor executor) {
        Assert.isTrue(corePoolSize <= maxPoolSize, "corePoolSize must be less than maxPoolSize");
        executor.setMaximumPoolSize(maxPoolSize);
    }
}
