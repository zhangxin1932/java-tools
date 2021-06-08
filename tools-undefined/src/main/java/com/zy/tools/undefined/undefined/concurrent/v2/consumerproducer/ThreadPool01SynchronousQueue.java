package com.zy.tools.undefined.undefined.concurrent.v2.consumerproducer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPool01SynchronousQueue {

    private static final List<Integer> list = new CopyOnWriteArrayList<>();

    public static void main(String[] args) {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(1,
                1,
                0L,
                TimeUnit.SECONDS,
                new SynchronousQueue<>());

        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            System.out.println(Thread.currentThread().getName() + " --> hello");
            int finalI = i;
            try {
                // FIXME, 这里切记, 请视情况 --> 捕获异常
                executor.execute(() -> {
                    System.out.println(Thread.currentThread().getName() + " --> lll");
                    list.add(finalI);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                executor.execute(() -> System.out.println(Thread.currentThread().getName() + " --> >>>>>>>>>>>>>>>>>" + list));
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                TimeUnit.SECONDS.sleep(1L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
