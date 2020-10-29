package com.zy.tools.undefined.concurrent.v1;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CountDownLatch;

/*
 * CountDownLatch ：闭锁，在完成某些运算时，只有其他所有线程的运算全部完成，当前运算才继续执行
 */
public class TestCountDownLatch {

    public static void main(String[] args) {
        CountDownLatch latch = new CountDownLatch(10); //与下列for循环中线程数保持值
        LatchDemo ld = new LatchDemo(latch);
        Instant begin = Instant.now();
        for (int i = 0; i < 10; i ++){
            new Thread(ld).start();
        }
        try {
            latch.await(); // 上述代码执行完之前,下述代码处于等待状态
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Instant end = Instant.now();
        System.out.println("时间为:"+(Duration.between(begin, end).toMillis()) + "毫秒");
    }
}

class LatchDemo implements Runnable {

    private CountDownLatch latch;

    public LatchDemo(CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public void run() {
        synchronized (this){
            try {
                for (int i = 0; i < 100; i++) {
                    if (i % 3 == 0) {
                        System.out.println(i);
                    }
                }
            } finally {
                latch.countDown(); // 计数器递减
            }
        }
    }
}