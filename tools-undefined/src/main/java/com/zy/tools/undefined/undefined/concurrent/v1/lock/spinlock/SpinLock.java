package com.zy.tools.undefined.undefined.concurrent.v1.lock.spinlock;

import java.util.concurrent.atomic.AtomicReference;

public class SpinLock {
    // AtomicReference，CAS，compareAndSet保证了操作的原子性
    private AtomicReference<Thread> owner = new AtomicReference<>();

    public void lock() {
        // 如果锁未被占用，则设置当前线程为锁的拥有者，设置成功返回true，否则返回false
        // null为期望值，currentThread为要设置的值，如果当前内存值和期望值null相等，替换为currentThread
        while (!owner.compareAndSet(null, Thread.currentThread())) {
        }
    }

    public void unlock() {
        // 只有锁的拥有者才能释放锁，只有上锁的线程获取到的currentThread，才能和内存中的currentThread相等
        owner.compareAndSet(Thread.currentThread(), null);
    }
}
