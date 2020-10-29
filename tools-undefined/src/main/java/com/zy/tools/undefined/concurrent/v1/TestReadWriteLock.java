package com.zy.tools.undefined.concurrent.v1;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/*
 * 1. ReadWriteLock : 读写锁
 *
 * 写写/读写 需要“互斥”
 * 读读 不需要互斥
 *
 */
public class TestReadWriteLock {
    public static void main(String[] args) {
        ReadWriteLockDemo demo = new ReadWriteLockDemo();
        for (int i = 0; i < 10; i ++){
            new Thread(() -> {
                demo.read();
            }, "读线程:" + i).start();
        }
        new Thread(() -> demo.write(1), "写线程").start();
    }
}

class ReadWriteLockDemo{

    private int no = 0;

    private ReadWriteLock lock = new ReentrantReadWriteLock();

    // 读
    public void read(){
        lock.readLock().lock(); // 上读锁
        try {
            System.out.println(Thread.currentThread().getName() + ":" + no);
        } finally {
            lock.readLock().unlock(); // 释放锁
        }
    }

    // 写
    public void write(int no){
        lock.writeLock().lock(); // 上写锁
        try {
            this.no = no;
            System.out.println(Thread.currentThread().getName() + ":" + no);
        } finally {
            lock.writeLock().unlock(); // 释放锁
        }
    }
}
