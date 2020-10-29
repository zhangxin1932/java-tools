package com.zy.tools.undefined.concurrent.v1;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TestABC {
    public static void main(String[] args) {

        PrintABC printABC = new PrintABC();

        new Thread(() -> {
            for (int i = 1; i <= 10; i ++){
                printABC.printA(i);
            }
        }, "A").start();

        new Thread(() -> {
            for (int i = 1; i <= 10; i ++){
                printABC.printB(i);
            }
        }, "B").start();

        new Thread(() -> {
            for (int i = 1; i <= 10; i ++){
                printABC.printC(i);
            }
        }, "C").start();
    }
}

class PrintABC {

    private int no = 1;

    private Lock lock = new ReentrantLock();

    private Condition condition1 = lock.newCondition();
    private Condition condition2 = lock.newCondition();
    private Condition condition3 = lock.newCondition();

    // 打印A的方法
    public void printA(int count){
        lock.lock();
        try {
            // 判断是否到A
            while (no != 1){
                try {
                    condition1.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // 打印A
            for (int i = 0; i < 1; i ++){
                System.out.println(Thread.currentThread().getName() + "==========" + i + "=====" + count);
            }
            // 唤醒B
            no = 2;
            condition2.signal();
        }finally {
            lock.unlock();
        }
    }

    // 打印B的方法
    public void printB(int count){
        lock.lock();
        try{
            // 判断是否到B
            while (no != 2) {
                try {
                    condition2.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // 打印B
            for (int i = 0; i < 2; i ++){
                System.out.println(Thread.currentThread().getName() + "==========" + i + "=====" + count);
            }
            // 唤醒C
            no = 3;
            condition3.signal();
        }finally {
            lock.unlock();
        }
    }

    // 打印C的方法
    public void printC(int count){
        lock.lock();
        try{
            while (no != 3){
                try {
                    condition3.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // 打印C
            for (int i = 0; i < 3; i ++){
                System.out.println(Thread.currentThread().getName() + "==========" + i + "=====" + count);
            }
            System.out.println(">>>>>>>>>>>>>>>第"+ count + "轮结束<<<<<<<<<<<<<<<<<<<<<");
            // 唤醒A
            no = 1;
            condition1.signal();
        } finally {
            lock.unlock();
        }
    }
}
