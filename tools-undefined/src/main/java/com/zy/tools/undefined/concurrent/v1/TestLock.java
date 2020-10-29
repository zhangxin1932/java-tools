package com.zy.tools.undefined.concurrent.v1;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TestLock {

    public static void main(String[] args) {
        Ticket ticket = new Ticket();
        for (int i = 0; i < 10; i ++){
            new Thread(ticket).start();
        }
    }
}

/*
 * 一、用于解决多线程安全问题的方式：
 *
 * synchronized:隐式锁
 * 1. 同步代码块
 *
 * 2. 同步方法
 *
 * jdk 1.5 后：
 * 3. 同步锁 Lock
 * 注意：是一个显示锁，需要通过 lock() 方法上锁，必须通过 unlock() 方法进行释放锁
 *  ReentrantLock 实现了 Lock 接口，
 *  并提供了与synchronized 相同的互斥性和内存可见性。
 *  但相较于synchronized 提供了更高的处理锁的灵活性。
 */
class Ticket implements Runnable {

    private int ticket = 100;

    private Lock lock = new ReentrantLock();

    @Override
    public void run() {
        while (true) {
            lock.lock(); // 上锁
            try{
                if (ticket > 0){
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(Thread.currentThread().getName() + "完成售票,余票为:" + --ticket);
                }
            }finally {
                lock.unlock(); // 释放锁
            }
        }
    }
}