package com.zy.tools.undefined.concurrent.v1;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/*
 * volatile 关键字：当多个线程进行操作共享数据时，可以保证内存中的数据可见。
 * 					  相较于 synchronized 是一种较为轻量级的同步策略。
 *
 * 注意：
 * 1. volatile 不具备“互斥性”
 * 2. volatile 不能保证变量的“原子性”
 */
@Slf4j
public class TestVolatile {
    public static void main(String[] args) {
        ThreadDemo td = new ThreadDemo();
        new Thread(td).start();

        while (true) {
            if (td.isFlag()) {
                System.out.println("hello");
                break;
            }
        }
    }

    @Slf4j
    private static class ThreadDemo implements Runnable {

        @Getter
        @Setter
        private volatile boolean flag = false;

        @Override
        public void run() {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                log.error("==========");
            }
            flag = true;
            System.out.println(">>>>>>>>flag=" + flag + "<<<<<<<<<");
        }
    }

    /**
     * 重排序
     * 重排序是指编译器和处理器为了优化程序性能而对指令序列进行重新排序的一种手段。
     */
    private static class VolatileDemo02 {
        private static int a, b, x, y;

        /**
         * 这段代码的运行结果可能为(1,0)、(0,1)或(1,1)，(0,0)。
         * 因为线程 one 可以在线程 two 开始之前就执行完了，
         * 也有可能反之，甚至有可能二者的指令是同时或交替执行的。
         * 因为可能执行的顺序为 2341，程序执行时发生了重排序。
         *
         * @param args
         * @throws InterruptedException
         */
        public static void main(String[] args) throws InterruptedException {
            System.out.println("--------------------");
            for (int i = 0; i < 10000000; i++) {
                if (!testVolatile()) {
                    break;
                }
            }
            System.out.println("--------------------");
        }

        private static boolean testVolatile() throws InterruptedException {
            Thread one = new Thread(() -> {
                a = 1;    // step 1
                x = b;    // step 2
            });
            Thread two = new Thread(() -> {
                b = 1;    // step 3
                y = a;    // step 4
            });
            one.start();
            two.start();
            one.join();
            two.join();
            if (x != 0 || x == y) {
                System.out.println(String.format("(%d, %d)", x, y));
                return false;
            }
            return true;
        }
    }

    /**
     * 环境配置
     * java version "1.8.0_151"
     * Java(TM) SE Runtime Environment (build 1.8.0_151-b12)
     * Java HotSpot(TM) 64-Bit Server VM (build 25.151-b12, mixed mode)
     */
    private static class VolatileDemo03 {
        // 将这里改为 volatile, block1 处可以执行到, 不再是死循环了
        private static boolean flag = false;
        // 将这里的改为 volatile 或者 将 int 改为 Integer, block1 处可以执行到, 不再是死循环了
        private static int count = 0;

        public static void main(String[] args) {
            new Thread(() -> {
                try {
                    // 将这里的 sleep && 将下一行的打印语句 注释掉, block1 处可以执行到, 不再是死循环了
                    TimeUnit.SECONDS.sleep(2L);
                    flag = true;
                    System.out.println("flag 已经设置为: " + flag);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();

            while (!flag) {
                count++;
                // 在这里打印出 count 或者其他任意内容, block1 处可以执行到, 不再是死循环了
                // System.out.println(count);
            }
            // block1: 当前默认代码情况下, 这一行代码永远执行不到, 代码处于死循环中
            System.out.println("count=" + count);
        }
    }

}