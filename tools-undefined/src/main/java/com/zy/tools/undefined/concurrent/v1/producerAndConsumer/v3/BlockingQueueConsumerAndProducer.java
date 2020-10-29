package com.zy.tools.undefined.concurrent.v1.producerAndConsumer.v3;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class BlockingQueueConsumerAndProducer {

    public static void main(String[] args) throws InterruptedException {
        BlockingQueueConsumerAndProducerDemo demo = new BlockingQueueConsumerAndProducerDemo(new ArrayBlockingQueue<>(10));

        new Thread(() -> {
            try {
                System.out.println(Thread.currentThread().getName() + "生产线程启动");
                demo.produce();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "t1").start();

        new Thread(() -> {
            try {
                System.out.println(Thread.currentThread().getName() + "消费线程启动");
                demo.consume();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "t2").start();

        TimeUnit.SECONDS.sleep(5);
        demo.stop();

    }
}

class BlockingQueueConsumerAndProducerDemo {

    private volatile boolean RUNNING = true;
    private AtomicInteger num = new AtomicInteger();
    private BlockingQueue<String> blockingQueue;

    public BlockingQueueConsumerAndProducerDemo(BlockingQueue<String> blockingQueue) {
        this.blockingQueue = blockingQueue;
        System.out.println("传入的阻塞队列是: " + blockingQueue.getClass().getName());
    }

    public void produce() throws InterruptedException {
        boolean result;
        String data;
        while (RUNNING) {
            data = num.incrementAndGet() + "";
            result = blockingQueue.offer(data, 1L, TimeUnit.SECONDS);
            if (result) {
                System.out.println(Thread.currentThread().getName() + "线程成功生产" + data);
            } else {
                System.out.println(Thread.currentThread().getName() + "线程生产" + data + "失败");
            }
            TimeUnit.SECONDS.sleep(1L);
        }
        System.out.println(Thread.currentThread().getName() + "线程即将关闭, 不再生产");
    }

    public void consume() throws InterruptedException {
        String result;
        while (RUNNING) {
            result = blockingQueue.poll(1L, TimeUnit.SECONDS);
            if (result != null) {
                System.out.println(Thread.currentThread().getName() + "线程成功消费" + result);
            } else {
                RUNNING = false;
                System.out.println(Thread.currentThread().getName() + "线程消费" + result + "失败");
                return;
            }
        }
    }

    public void stop() {
        this.RUNNING = false;
    }

}
