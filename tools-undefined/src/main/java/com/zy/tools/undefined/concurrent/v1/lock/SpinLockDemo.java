package com.zy.tools.undefined.concurrent.v1.lock;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class SpinLockDemo {

    private static class QueuedSpinLock {

        private AtomicBoolean queuedSpinLock = new AtomicBoolean(false);
        public boolean tryLock() {
            // 尝试获取锁, 成功返回 true, 失败返回 false
            return queuedSpinLock.compareAndSet(false, true);
        }
        public void unLock() {
            if (!queuedSpinLock.compareAndSet(true, false)) {
                throw new RuntimeException("释放锁失败");
            }
        }

        public void lock() {
            // 循环检测尝试获取锁
            while (!tryLock()) {
                // do something
                System.out.println("....");
            }
        }

    }

    private static class TicketSpinLock {
        // 队列票据(当前排队号码)
        private AtomicLong currentQueueNo = new AtomicLong(0L);
        // 出队票据(当前需等待号码)
        private AtomicLong waitedQueueNo = new AtomicLong(0L);
        private ThreadLocal<Long> ticketThreadLocal = new ThreadLocal<>();
        public void lock() {
            long currentTicketNo = waitedQueueNo.incrementAndGet();
            // 获取锁的时候，将当前线程的排队号保存起来
            ticketThreadLocal.set(currentTicketNo);
            while (currentTicketNo != currentQueueNo.get()) {
                // doSomething...
            }
        }

        public void releaseLock() {
            Long currentTicketNo = ticketThreadLocal.get();
            currentQueueNo.compareAndSet(currentTicketNo, currentTicketNo + 1);
        }
    }

}
