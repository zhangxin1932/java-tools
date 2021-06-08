package com.zy.tools.undefined.undefined.concurrent.v1;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

public interface TestForkJoinPool {

    public static ExecutorService newCachedThreadPool(Object... params) { // 该方法可以传入参数,以占位下述7个参数
        return new ThreadPoolExecutor(
                0, // 线程池核心池的大小,即闲时线程池中线程数量
                Integer.MAX_VALUE, // 线程池的最大线程数
                60L, // 当线程数大于核心时，此为终止前多余的空闲线程等待新任务的最长时间。
                TimeUnit.SECONDS, // 这里的时间单位可以选择其他
                new SynchronousQueue<Runnable>(), // 这里的阻塞队列可以选择其他类型
                Executors.defaultThreadFactory(),  // 这里的线程工厂参数可以自定义
                new ThreadPoolExecutor.DiscardPolicy()  // 这里的拒绝策略可以选择其他类型
        );
    }

    /*
     * 在调用shutdown()方法之后，
     * ExecutorService不会立即关闭，但是它不再接收新的任务，直到当前所有线程执行完成才会关闭，
     * 所有在shutdown()执行之前提交的任务都会被执行
     */
    void shutdown();

    /*
     * 立即关闭ExecutorService, 这个动作将跳过所有正在执行的任务和被提交还没有执行的任务。
     * 但是它并不对正在执行的任务做任何保证，有可能它们都会停止，也有可能执行完成
     */
    List<Runnable> shutdownNow();

    boolean isShutdown();

    boolean isTerminated();

    boolean awaitTermination(long var1, TimeUnit var3) throws InterruptedException;

    // 执行有返回值的线程。返回Future对象
    <T> Future<T> submit(Callable<T> var1);

    // 如果线程执行成功则返回预设的result
    <T> Future<T> submit(Runnable var1, T var2);

    // 执行没有返回值的线程并返回Future对象
    Future<?> submit(Runnable var1);

    // 执行一个集合的有返回值的线程
    <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> var1) throws InterruptedException;

    /*
     * 在指定的时间内执行集合的方法，如果指定时间内还没有获取结果，那么终止该线程执行。
     * 返回的Future对象可通过isDone()方法和isCancel()来判断是执行成功还是被终止了
     */
    <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> var1, long var2, TimeUnit var4) throws InterruptedException;

    /*
    * 方法接收的是一个Callable的集合，执行这个方法会返回所有Callable任务中其中一个任务的执行结果。
    * 在任意一个任务成功（或ExecutorService被中断/超时）后就会返回
    */
    <T> T invokeAny(Collection<? extends Callable<T>> var1) throws InterruptedException, ExecutionException;

    /*
    * 在指定的时间内执行集合的方法，如果指定时间内还没有获取结果，那么终止该线程执行。
    * 返回的Future对象可通过isDone()方法和isCancel()来判断是执行成功还是被终止了
    * 在任意一个任务成功（或ExecutorService被中断/超时）后就会返回
    */
    <T> T invokeAny(Collection<? extends Callable<T>> var1, long var2, TimeUnit var4) throws InterruptedException, ExecutionException, TimeoutException;
}