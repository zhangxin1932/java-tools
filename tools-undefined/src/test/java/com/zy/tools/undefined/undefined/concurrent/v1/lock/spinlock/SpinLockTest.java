package com.zy.tools.undefined.undefined.concurrent.v1.lock.spinlock;

import org.junit.Test;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SpinLockTest {
    private int count = 0;
    private ExecutorService executorService = Executors.newCachedThreadPool();
    private Lock reentrantLock = new ReentrantLock();

    @Test
    public void fn01() throws InterruptedException {
        SpinLock lock = new SpinLock();
        final CyclicBarrier cb = new CyclicBarrier(10, () -> System.out.println(count));
        for (int i = 0; i < 10; i++) {
            executorService.submit(() -> {
                // 方法1: 测试自定义的 Lock
                try {
                    lock.lock();
                    for (int j = 0; j < 20000000; j++) {
                        ++count;
                    }
                } finally {
                    lock.unlock();
                }
                // 方法2: 测试无锁时, 并发问题
                /*for (int j = 0; j < 10000000; j++) {
                    ++count;
                }*/
                try {
                    cb.await();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        TimeUnit.SECONDS.sleep(3L);
        executorService.shutdown();
    }

    @Test
    public void fn02() throws InterruptedException {
        ClhSpinLock clh = new ClhSpinLock();
        final CyclicBarrier cb = new CyclicBarrier(10, () -> System.out.println(count));
        for (int i = 0; i < 10; i++) {
            executorService.submit(() -> {
                // 方法1: 测试自定义的 CLH Lock:
                testLock(clh);
                // 方法2: 测试 JDK Lock testLock(reentrantLock);
                // 方法3: 测试无锁时, 并发问题
                /*for (int j = 0; j < 10000000; j++) {
                    ++count;
                }*/
                try {
                    cb.await();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        TimeUnit.SECONDS.sleep(3L);
        executorService.shutdown();
    }

    private void testLock(Lock lock) {
        try {
            lock.lock();
            for (int i = 0; i < 10000000; i++) {
                ++count;
            }
        } finally {
            lock.unlock();
        }
    }
}
