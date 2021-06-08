package com.zy.tools.undefined.undefined.concurrent.v2.consumerproducer;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ABCTest {

    public static void main(String[] args) {
        PrintTask printTask = new PrintTask();

        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                printTask.printA();
            }
        }).start();

        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                printTask.printB();
            }
        }).start();

        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                printTask.printC();
            }
        }).start();

    }

    private static class PrintTask {
        private final Lock lock = new ReentrantLock();
        private final Condition conditionA = lock.newCondition();
        private final Condition conditionB = lock.newCondition();
        private final Condition conditionC = lock.newCondition();
        private int flag = 1;

        public void printA() {
            lock.lock();
            try {
                while (flag != 1) {
                    try {
                        conditionA.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("A");
                flag = 2;
                conditionB.signal();
            } finally {
                lock.unlock();
            }
        }

        public void printB() {
            lock.lock();
            try {
                while (flag != 2) {
                    try {
                        conditionB.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("B");
                flag = 3;
                conditionC.signal();
            } finally {
                lock.unlock();
            }
        }

        public void printC() {
            lock.lock();
            try {
                while (flag != 3) {
                    try {
                        conditionC.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("C");
                flag = 1;
                conditionA.signal();
            } finally {
                lock.unlock();
            }
        }
    }

}
