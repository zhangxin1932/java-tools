package com.zy.tools.undefined.undefined.concurrent.v1.producerAndConsumer.v2;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TestProducerAndConsumerLock {

    public static void main(String[] args) {
        Clerk01 clerk = new Clerk01();
        Producer01 producer = new Producer01(clerk);
        Consumer01 consumer = new Consumer01(clerk);
        new Thread(producer, "生产者甲").start();
        new Thread(consumer, "消费者甲").start();
        new Thread(producer, "生产者乙").start();
        new Thread(consumer, "消费者乙").start();
    }
}

/**
 * 店员
 */
class Clerk01 {

    // 共享资源
    private int product = 0;

    // 同步锁
    private Lock lock = new ReentrantLock();

    private Condition condition = lock.newCondition();

    // 进货方法
    public void purchase(){
        lock.lock();
        try{
            while (product >= 10) { //为了避免虚假唤醒问题，应该总是使用在while循环中,此处不能用if
                System.out.println("满仓了,停止进货");
                try {
                    condition.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            ++product;
            System.out.println(Thread.currentThread().getName() + " : " + product);
            condition.signalAll();
        }finally {
            lock.unlock();
        }
    }

    // 卖货方法
    public void sale(){
        lock.lock();
        try{
            while (product <= 0){ //为了避免虚假唤醒问题，应该总是使用在while循环中,此处不能用if
                System.out.println("没货了,请等待");
                try {
                    condition.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            --product;
            System.out.println(Thread.currentThread().getName() + " : " + product);
            condition.signalAll();
        }finally {
            lock.unlock();
        }

    }
}

/**
 * 生产者
 */
class Producer01 implements Runnable{

    private Clerk01 clerk;

    public Producer01(Clerk01 clerk){
        this.clerk = clerk;
    }

    @Override
    public void run() {
        for (int i = 0; i < 10; i ++){
            clerk.purchase();
        }
    }
}

/**
 * 消费者
 */
class Consumer01 implements Runnable {

    private Clerk01 clerk;

    public Consumer01(Clerk01 clerk){
        this.clerk = clerk;
    }

    @Override
    public void run() {
        for (int i = 0; i < 10; i++){
            clerk.sale();
        }
    }
}
