package com.zy.tools.undefined.concurrent.v2.consumerproducer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ConsumerAndProducer02 {

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
        private final Lock lock = new ReentrantLock();
        private final Condition condition = lock.newCondition();

        public void goodsOut() {
            lock.lock();
            // 当进入 lock 块时, 表示当前线程[获取到对象锁]
            // 如果没有进入 lock 块, 则表示获取对象锁失败, 线程状态为 BLOCKED
            try {
                // 为了避免虚假唤醒问题，应该总是使用在while循环中,此处不能用if
                while (count <= 0) {
                    try {
                        System.out.println(Thread.currentThread().getName() + " ==> 库存不足, 请等待");
                        condition.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                count--;
                System.out.println(Thread.currentThread().getName() + " ==> 消费了商品, 当前剩余库存: " + count);
                // 当调用 await 方法(不带超时时间)时, 当前线程[释放此对象锁], 线程状态为 WAITING, 其他线程可继续争抢锁
                // 当调用 await 方法(带超时时间)时, 当前线程[释放此对象锁], 线程状态为 TIME_WAITING, 其他线程可继续争抢锁
                // 调用完 await 方法后, 当前线程挂起, 需等待[其他线程调用 signal/signalAll 方法], 才能[唤起此线程], 执行 wait 之后的代码
                condition.signalAll();
            } finally {
                lock.unlock();
            }
        }

        public void goodsIn() {
            lock.lock();
            // 当进入 lock 块时, 表示当前线程[获取到对象锁]
            // 如果没有进入 lock 块, 则表示获取对象锁失败, 线程状态为 BLOCKED
            try {
                // 为了避免虚假唤醒问题，应该总是使用在while循环中,此处不能用if
                while (count >= 30) {
                    try {
                        System.out.println(Thread.currentThread().getName() + " ==> 库存已达到 30, 停止进货");
                        // 当调用 await 方法(不带超时时间)时, 当前线程[释放此对象锁], 线程状态为 WAITING, 其他线程可继续争抢锁
                        // 当调用 await 方法(带超时时间)时, 当前线程[释放此对象锁], 线程状态为 TIME_WAITING, 其他线程可继续争抢锁
                        // 调用完 await 方法后, 当前线程挂起, 需等待[其他线程调用 signal/signalAll 方法], 才能[唤起此线程], 执行 wait 之后的代码
                        condition.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                count++;
                System.out.println(Thread.currentThread().getName() + " ==> 生产了商品, 当前剩余库存: " + count);
                // 调用完 signalAll 方法后, 唤起所有 当前对象锁下 线程状态为 WAITING/TIME_WAITING 的线程
                condition.signalAll();
            } finally {
                lock.unlock();
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
