package com.zy.tools.undefined.concurrent.v1.threadPool;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

public class FixedThreadPoolExecutorInstance {

    private static FixedThreadPoolExecutorInstance threadPoolDemo = new FixedThreadPoolExecutorInstance();
    private FixedThreadPoolExecutor fixedThreadPoolExecutor;
    private static int CORE_POOL_SIZE = 3;
    private static int MAX_POOL_SIZE = 3;
    private static int BLOCKING_QUEUE_SIZE = 1024 * 2;

    private FixedThreadPoolExecutorInstance() {
        fixedThreadPoolExecutor = new FixedThreadPoolExecutor(CORE_POOL_SIZE,
                MAX_POOL_SIZE,
                0L,
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(BLOCKING_QUEUE_SIZE));
    }

    public static FixedThreadPoolExecutorInstance getInstance() {
        return threadPoolDemo;
    }

    public void run(Runnable runnable) {
        fixedThreadPoolExecutor.execute(runnable);
    }

}
