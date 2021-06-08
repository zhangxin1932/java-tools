package com.zy.tools.undefined.undefined.concurrent.v1.lock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockFairLock {

    public static void main(String[] args) {
        // ReentrantLockDemo reentrantLockDemo = new ReentrantLockDemo();
        SynchronizedDemo synchronizedDemo = new SynchronizedDemo();
        new Thread(() -> {
            synchronizedDemo.sendMsg();
        }, "t1").start();

        new Thread(() -> {
            synchronizedDemo.sendMsg();
        }, "t2").start();
    }

    private static class SynchronizedDemo {
        public synchronized void sendMsg() {
            System.out.println(Thread.currentThread().getName() + "---invoke sendMsg()");
            sendEmail();
        }

        private synchronized void sendEmail() {
            System.out.println(Thread.currentThread().getName() + "---invoke sendEmail()");
        }
    }

    private static class ReentrantLockDemo implements Runnable {

        private Lock lock = new ReentrantLock();

        @Override
        public void run() {
            sendMsg();
        }

        private void sendMsg() {
            lock.lock();
            try {
                System.out.println(Thread.currentThread().getName() + "---invoke sendMsg()");
                sendEmail();
            } finally {
                lock.unlock();
            }
        }

        private void sendEmail() {
            lock.lock();
            try {
                System.out.println(Thread.currentThread().getName() + "---invoke sendEmail()");
            } finally {
                lock.unlock();
            }
        }
    }
}
