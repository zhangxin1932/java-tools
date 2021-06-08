package com.zy.tools.undefined.undefined.concurrent.v1.lock;

import java.util.concurrent.CountDownLatch;

public class CountDownLatchDemo {

    public static void main(String[] args) throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(5);
        for (int i = 0; i < 5; i ++) {
            new Thread(() -> {
                System.out.println(Thread.currentThread().getName() + "号线程离开");
                countDownLatch.countDown();
            }, String.valueOf(i)).start();
        }

        countDownLatch.await();
        System.out.println("----Main线程最后离开");
    }
}
