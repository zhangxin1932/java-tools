package com.zy.tools.undefined.undefined.concurrent.v1.blocking;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

public class SynchronousQueueDemo {

    public static void main(String[] args) {
        // 同步队列, 生产一个, 消费一个
        BlockingQueue<String> blockingQueue = new SynchronousQueue<>();

        new Thread(() -> {
            try {
                System.out.println(Thread.currentThread().getName() + "线程put-->a");
                blockingQueue.put("a");
                System.out.println(Thread.currentThread().getName() + "线程put-->b");
                blockingQueue.put("b");
                System.out.println(Thread.currentThread().getName() + "线程put-->c");
                blockingQueue.put("c");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "t1").start();

        new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(2);
                System.out.println(Thread.currentThread().getName() + "线程take-->" + blockingQueue.take());
                System.out.println(Thread.currentThread().getName() + "线程take-->" + blockingQueue.take());
                System.out.println(Thread.currentThread().getName() + "线程take-->" + blockingQueue.take());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "t2").start();

    }

}
