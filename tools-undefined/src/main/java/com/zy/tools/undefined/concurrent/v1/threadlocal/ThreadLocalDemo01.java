package com.zy.tools.undefined.concurrent.v1.threadlocal;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ThreadLocalDemo01 {
    private static final ExecutorService executor = Executors.newCachedThreadPool();
    private static ThreadLocal<String> threadLocal = new ThreadLocal<>();
    public static void main(String[] args) throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(4);
        for (int i = 1; i < 5; i++) {
            int finalI = i;
            executor.submit(() -> {
                try {
                    threadLocal.set(Thread.currentThread().getName() + " ---> " + finalI);
                    System.out.println(threadLocal.get());
                    countDownLatch.countDown();
                } finally {
                    threadLocal.remove();
                }
            });
        }
        countDownLatch.await(5L, TimeUnit.SECONDS);
        executor.shutdown();
    }
}
