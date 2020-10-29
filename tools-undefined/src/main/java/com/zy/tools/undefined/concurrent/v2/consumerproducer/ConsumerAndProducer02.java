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
            try {
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
                condition.signalAll();
            } finally {
                lock.unlock();
            }
        }

        public void goodsIn() {
            lock.lock();
            try {
                while (count >= 30) {
                    try {
                        System.out.println(Thread.currentThread().getName() + " ==> 库存已达到 30, 停止进货");
                        condition.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                count++;
                System.out.println(Thread.currentThread().getName() + " ==> 生产了商品, 当前剩余库存: " + count);
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
