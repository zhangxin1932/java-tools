package com.zy.tools.undefined.concurrent.v2.consumerproducer;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 这里旨在通过一个反例, 演示线程使用错误可能带来的问题
 */
public class PrintABC {

    /**
     * 前置条件:
     * 1.启动 3 个打印 A,B,C 的延时任务, 分别延时 1s, 2s, 3s
     * 2.这里设置核心线程数为 2, 小于 3
     * 3.运行 main 方法
     *
     * 现象:
     * 1.大多数时候, 交替打印 A,B,C 几轮后(或者只打印了一轮 A,B), 线程卡住, 不再打印任何一个字母
     * 2.线程卡住后,  main 方法里的 while(true){} 打印 flag 为 3
     * A
     * B
     * C
     * A
     * B
     * C
     * A
     * B
     * flag is: 3
     * C
     * A
     * B
     * flag is: 3
     * flag is: 3
     * ...
     *
     * 问题: 为何线程会卡住?
     *
     * 分析:
     * 1.由于 3 个延时任务的延迟时间不同, 3个延时任务对应的处理调用的频率不同
     * 2.当执行到某一次时, 假定 flag 为 3
     * 2.1 线程1 执行 printA 并获取到锁, 但 flag 不为 1, 则执行 await 方法, 线程挂起
     * 2.2 线程2 执行 printB 并获取到锁, 但 flag 不为 2, 则执行 await 方法, 线程挂起
     * 2.3 由于线程池中共两个核心线程, 此时这俩线程挂起, 没有其他线程来唤起这俩线程. 所以就造成线程卡死的现象.
     *
     * 分析工具及步骤:
     * 1.通过 jps 查看这个进程号为: 6136
     * 2.通过 jstack -l 6136 > /printABCDump.txt
     * 3.启动 IBM-TMDA 工具: java -jar jca467.jar
     * 4.导入上一步生成的线程 dump文件
     * 5.分析可知:
     * 这里看到, 这两个线程状态均是 waiting on condition, 调用了 LockSupport.park方法, 即线程挂起了,
     * 通过堆栈信息可知, 分别是在 printA 和 printB 方法中的 await 方法所致.
     * 这俩线程均在等待被其他线程 唤起, 但是, 没有多余的线程来唤起他们了 ... so, 杯具了
     * @param args
     */
    public static void main(String[] args) {
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(2);
        Worker worker = new Worker();
        scheduledExecutorService.scheduleWithFixedDelay(worker::printA, 0L, 1L, TimeUnit.SECONDS);
        scheduledExecutorService.scheduleWithFixedDelay(worker::printB, 0L, 2L, TimeUnit.SECONDS);
        scheduledExecutorService.scheduleWithFixedDelay(worker::printC, 0L, 3L, TimeUnit.SECONDS);

        while (true) {
            try {
                TimeUnit.SECONDS.sleep(5L);
                System.out.println("flag is: " + worker.getContent());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static class Worker {
        private int flag = 1;
        private final Lock lock = new ReentrantLock();
        private final Condition conditionA = lock.newCondition();
        private final Condition conditionB = lock.newCondition();
        private final Condition conditionC = lock.newCondition();

        public void printA() {
            boolean b = lock.tryLock();
            if (!b) {
                return;
            }
            try {
                while (flag != 1) {
                    conditionA.await();
                }
                System.out.println("A");
                flag = 2;
                conditionB.signal();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }

        public void printB() {
            boolean b = lock.tryLock();
            if (!b) {
                return;
            }
            try {
                while (flag != 2) {
                    conditionB.await();
                }
                System.out.println("B");
                flag = 3;
                conditionC.signal();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }

        public void printC() {
            boolean b = lock.tryLock();
            if (!b) {
                return;
            }
            try {
                while (flag != 3) {
                    conditionC.await();
                }
                System.out.println("C");
                flag = 1;
                conditionA.signal();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }

        public int getContent() {
            return flag;
        }

    }

}
