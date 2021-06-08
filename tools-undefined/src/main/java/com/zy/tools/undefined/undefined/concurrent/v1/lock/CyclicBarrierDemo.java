package com.zy.tools.undefined.undefined.concurrent.v1.lock;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class CyclicBarrierDemo {

    public static void main(String[] args) throws BrokenBarrierException, InterruptedException {
        CyclicBarrier cyclicBarrier = new CyclicBarrier(5, () -> System.out.println("所有人都已到达, 可以开会"));

        for (int i = 0; i < 5; i ++) {
            new Thread(() -> {
                System.out.println(Thread.currentThread().getName() + "号线程到达");
                try {
                    cyclicBarrier.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }, String.valueOf(i)).start();
        }
    }
}
