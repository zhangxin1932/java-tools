package com.zy.tools.undefined.undefined.concurrent.v2;

public class DeadLockTest {

    public static void main(String[] args) {
        Worker worker = new Worker();

        new Thread(() -> worker.methodA()).start();
        new Thread(() -> worker.methodB()).start();
    }

    private static class Worker {
        private final Object lockA = new Object();
        private final Object lockB = new Object();

        public void methodA() {
            synchronized (lockA) {
                System.out.println(Thread.currentThread().getName() + " ==========> 获得了 lockA.");
                synchronized (lockB) {
                    System.out.println(Thread.currentThread().getName() + " ==========> 获得了 lockB.");
                }
            }
        }

        public void methodB() {
            synchronized (lockB) {
                System.out.println(Thread.currentThread().getName() + " ............ 获得了 lockB.");
                synchronized (lockA) {
                    System.out.println(Thread.currentThread().getName() + " ............ 获得了 lockA.");
                }
            }
        }
    }
}
