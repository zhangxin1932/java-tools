package com.zy.tools.undefined.concurrent.v1.threadPool;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
@Slf4j
public class FixedThreadPoolExecutor extends ThreadPoolExecutor {

    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();
    private int maxPoolSize;
    private int blockingQueueSize;

    public FixedThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
        this.maxPoolSize = maximumPoolSize;
        this.blockingQueueSize = workQueue.size();
    }

    /**
     * 可用线程池来处理消息等
     * 此时, 若最大线程池已满, 阻塞队列也已满, 则进行等待, 防止任务丢失等
     * @param command
     */
    @Override
    public void execute(Runnable command) {
        try {
            lock.lock();
            super.execute(command);
            if (this.getMaximumPoolSize() > maxPoolSize || this.getQueue().size() > blockingQueueSize - 1) {
                condition.await();
            }
        } catch (Exception e) {
            log.error("error to execute task", e);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void shutdown() {
        try {
            lock.lock();
            super.shutdown();
        } finally {
            lock.unlock();
        }
    }
}
