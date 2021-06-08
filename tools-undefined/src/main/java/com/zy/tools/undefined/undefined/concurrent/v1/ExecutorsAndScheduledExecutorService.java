package com.zy.tools.undefined.undefined.concurrent.v1;

import org.junit.Test;

import java.util.concurrent.*;

/*
 * 一、线程池：提供了一个线程队列，队列中保存着所有等待状态的线程。避免了创建与销毁额外开销，提高了响应的速度。
 *
 * 二、线程池的体系结构：
 * 	java.util.concurrent.Executor : 负责线程的使用与调度的根接口
 * 		|--**ExecutorService 子接口: 线程池的主要接口
 * 			|--ThreadPoolExecutor 线程池的实现类
 * 			|--ScheduledExecutorService 子接口：负责线程的调度
 * 				|--ScheduledThreadPoolExecutor ：继承 ThreadPoolExecutor， 实现 ScheduledExecutorService
 *
 * 三、工具类 : Executors
 * ExecutorService newFixedThreadPool() : 创建固定大小的线程池
 * ExecutorService newCachedThreadPool() : 缓存线程池，线程池的数量不固定，可以根据需求自动的更改数量。
 * ExecutorService newSingleThreadExecutor() : 创建单个线程池。线程池中只有一个线程
 *
 * ScheduledExecutorService newScheduledThreadPool() : 创建固定大小的线程，可以延迟或定时的执行任务。
 */
public class ExecutorsAndScheduledExecutorService {

    @Test
    public void fn01(){
        // 创建固定大小的线程池
        ExecutorService pool = Executors.newFixedThreadPool(10);
        // 实现Callable接口的线程池
        Future<Integer> future = pool.submit(() -> {
            int i = (int) (Math.random() * 100);
            for (int k = 0; i < i; i ++){
                i += k;
            }
            return i;
        });
        try {
            Integer num = future.get();
            System.out.println(num);
            // 关闭线程池
            pool.shutdown();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void fn02(){
        // 创建线程任务调度
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(5);
        // 实现Callable接口的线程池并定时调度
        ScheduledFuture<Integer> future = executor.schedule(() -> {
            int i = (int) (Math.random() * 100);
            for (int k = 0; i < i; i++) {
                i += k;
            }
            System.out.println(">>>>>>>>>>>>"+ i +"<<<<<<<<<<<");
            return i;
        }, 3, TimeUnit.SECONDS);

        try {
            System.out.println(future.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

    }

}
