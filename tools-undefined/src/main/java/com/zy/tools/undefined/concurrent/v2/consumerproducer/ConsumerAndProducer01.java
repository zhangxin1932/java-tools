package com.zy.tools.undefined.concurrent.v2.consumerproducer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConsumerAndProducer01 {

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newCachedThreadPool();

        Clerk clerk = new Clerk();

        for (int i = 0; i < 20; i++) {
            executorService.submit(new Consumer(clerk));
        }

        for (int i = 0; i < 40; i++) {
            executorService.submit(new Producer(clerk));
        }
    }


    private static class Clerk {
        private int count = 0;
        private final Object lock = new Object();

        public void goodsOut() {
            // synchronized (this) { // this 与 任意对象锁 (此处是 lock 对象), 结果稍有不同, 自行体会
            // 当进入 synchronized 块时, 表示当前线程获取到对象锁
            // 如果没有进入 synchronized 块, 则表示获取对象锁失败, 线程状态为 BLOCKED
            synchronized (lock) {
                // 为了避免虚假唤醒问题，应该总是使用在while循环中,此处不能用if
                while (count <= 0) {
                    try {
                        System.out.println(Thread.currentThread().getName() + " ==> 库存不足, 请等待");
                        // 当调用 wait 方法(不带超时时间)时, 当前线程[释放此对象锁], 线程状态为 WAITING, 其他线程可继续争抢锁
                        // 当调用 wait 方法(带超时时间)时, 当前线程[释放此对象锁], 线程状态为 TIME_WAITING, 其他线程可继续争抢锁
                        // 调用完 wait 方法后, 当前线程挂起, 需[等待其他线程]调用 notify/notifyAll 方法, 才能[唤起此线程], 执行 wait 之后的代码
                        wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                count--;
                System.out.println(Thread.currentThread().getName() + " ==> 消费了商品, 当前剩余库存: " + count);
                // 调用完 notifyAll 方法后, 唤起所有 当前对象锁下 线程状态为 WAITING/TIME_WAITING 的线程
                notifyAll();
            }
        }

        public void goodsIn() {
            // synchronized (this) { // this 与 任意对象锁 (此处是 lock 对象), 结果稍有不同, 自行体会
            // 当进入 synchronized 块时, 表示当前线程获取到对象锁
            // 如果没有进入 synchronized 块, 则表示获取对象锁失败, 线程状态为 BLOCKED
            synchronized (lock) {
                // 为了避免虚假唤醒问题，应该总是使用在while循环中,此处不能用if
                while (count >= 30) {
                    try {
                        System.out.println(Thread.currentThread().getName() + " ==> 库存已达到 30, 停止进货");
                        // 当调用 wait 方法(不带超时时间)时, 当前线程[释放此对象锁], 线程状态为 WAITING, 其他线程可继续争抢锁
                        // 当调用 wait 方法(带超时时间)时, 当前线程[释放此对象锁], 线程状态为 TIME_WAITING, 其他线程可继续争抢锁
                        // 调用完 wait 方法后, 当前线程挂起, 需[等待其他线程]调用 notify/notifyAll 方法, 才能[唤起此线程], 执行 wait 之后的代码
                        wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                count++;
                System.out.println(Thread.currentThread().getName() + " ==> 生产了商品, 当前剩余库存: " + count);
                // 调用完 notifyAll 方法后, 唤起所有 当前对象锁下 线程状态为 WAITING/TIME_WAITING 的线程
                notifyAll();
            }
        }
    }

    private static class Producer implements Runnable {

        private final Clerk clerk;

        private Producer(Clerk clerk) {
            this.clerk = clerk;
        }

        @Override
        public void run() {
            clerk.goodsIn();
        }
    }

    private static class Consumer implements Runnable {

        private final Clerk clerk;

        private Consumer(Clerk clerk) {
            this.clerk = clerk;
        }

        @Override
        public void run() {
            clerk.goodsOut();
        }
    }
}
