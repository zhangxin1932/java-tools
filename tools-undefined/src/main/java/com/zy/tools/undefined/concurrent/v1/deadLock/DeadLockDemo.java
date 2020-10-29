package com.zy.tools.undefined.concurrent.v1.deadLock;

public class DeadLockDemo {

    public static void main(String[] args) {
        LockDemo demoA = new LockDemo(true);
        LockDemo demoB = new LockDemo(false);
        new Thread(demoA, "demoA").start();
        new Thread(demoB, "demoB").start();
    }
}

class LockDemo implements Runnable {

    private final static Object lockA = new Object();
    private final static Object lockB = new Object();
    private boolean isA;

    public LockDemo(boolean isA) {
        this.isA = isA;
    }

    @Override
    public void run() {
        if (isA) {
            methodA();
        } else {
            methodB();
        }
    }

    public void methodA() {
        synchronized (lockA) {
            System.out.println(Thread.currentThread().getName() + "获取lockA, 尝试获得lockB");
            synchronized (lockB) {
                System.out.println(Thread.currentThread().getName() + "获取lockB");
            }
        }
    }

    public void methodB() {
        synchronized (lockB) {
            System.out.println(Thread.currentThread().getName() + "获取lockB, 尝试获得lockA");
            synchronized (lockA) {
                System.out.println(Thread.currentThread().getName() + "获取lockA");
            }
        }
    }

}