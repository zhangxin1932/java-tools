package com.zy.commons.lang.concurrent;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;
import java.util.concurrent.atomic.LongAdder;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class BlockedAndFixedThreadPoolExecutor extends ThreadPoolExecutor {
    private ReentrantLock lock = new ReentrantLock();
    private Condition condition;
    private static final int BLOCKING_QUEUE_SIZE = 1024 * 2;
    @Getter
    private final int blockingQueueSize;

    public BlockedAndFixedThreadPoolExecutor(int poolSize, int blockingQueueSize, String threadPrefix) {
        super(poolSize, poolSize, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>(blockingQueueSize <= 0 ? BLOCKING_QUEUE_SIZE : blockingQueueSize), new ThreadFactory() {
            private final LongAdder threadNo = new LongAdder();
            @Override
            public Thread newThread(Runnable r) {
                threadNo.increment();
                String threadName = threadPrefix + threadNo;
                Thread thread = new Thread(r, threadName);
                thread.setDaemon(true);
                return thread;
            }
        });
        this.blockingQueueSize = blockingQueueSize <= 0 ? BLOCKING_QUEUE_SIZE : blockingQueueSize;
        this.condition = this.lock.newCondition();
    }

    @Override
    public void execute(Runnable command) {
        try {
            this.lock.lock();
            super.execute(command);
            if (this.getPoolSize() == this.getMaximumPoolSize() && this.getQueue().size() >= this.getBlockingQueueSize()) {
                // 这里需要考虑下, 是否应该加上这个 3s 的超时时间
                this.condition.await(3L, TimeUnit.SECONDS);
            }
        } catch (Throwable e) {
            log.warn("failed to execute task.", e);
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        try {
            this.lock.lock();
            this.condition.signal();
        } finally {
            this.lock.unlock();
        }
    }
}
