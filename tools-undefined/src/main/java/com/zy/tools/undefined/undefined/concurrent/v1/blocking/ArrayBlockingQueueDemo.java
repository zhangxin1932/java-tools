package com.zy.tools.undefined.undefined.concurrent.v1.blocking;

import org.junit.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class ArrayBlockingQueueDemo {

    @Test
    public void fn01() {
        // 1.抛出异常
        BlockingQueue<String> blockingQueue = new ArrayBlockingQueue<>(2);

        // blockingQueue.remove();  // java.util.NoSuchElementException
        // blockingQueue.element();  // java.util.NoSuchElementException

        blockingQueue.add("a");
        blockingQueue.add("b");
        // blockingQueue.add("c");  // java.lang.IllegalStateException: Queue full

        blockingQueue.remove();
    }

    @Test
    public void fn02() {
        // 2.返回特殊值
        BlockingQueue<String> blockingQueue = new ArrayBlockingQueue<>(2);

        System.out.println(blockingQueue.poll());  // null
        System.out.println(blockingQueue.peek());  // null

        System.out.println(blockingQueue.offer("a"));  // true
        System.out.println(blockingQueue.offer("b"));  // true
        System.out.println(blockingQueue.offer("c"));  // false
    }

    @Test
    public void fn03() throws InterruptedException {
        // 3.一直阻塞-->慎用
        BlockingQueue<String> blockingQueue = new ArrayBlockingQueue<>(2);

        blockingQueue.put("a");
        blockingQueue.put("b");
        // blockingQueue.put("c");  // 线程会一直阻塞在这里

        System.out.println(blockingQueue.take());
        System.out.println(blockingQueue.take());
        // blockingQueue.take();  // 线程会一直阻塞在这里
    }

    @Test
    public void fn04() throws InterruptedException {
        // 4.超时退出
        BlockingQueue<String> blockingQueue = new ArrayBlockingQueue<>(2);

        System.out.println(blockingQueue.poll(1, TimeUnit.SECONDS));  // null

        System.out.println(blockingQueue.offer("a", 1, TimeUnit.SECONDS)); // true
        System.out.println(blockingQueue.offer("b", 1, TimeUnit.SECONDS)); // true
        System.out.println(blockingQueue.offer("c", 1, TimeUnit.SECONDS));  // false

        System.out.println(blockingQueue.poll(1, TimeUnit.SECONDS));  // a

    }
}
