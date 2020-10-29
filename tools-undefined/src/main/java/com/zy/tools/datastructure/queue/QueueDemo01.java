package com.zy.tools.datastructure.queue;

import org.junit.Test;

import java.util.Arrays;

/**
 * 数据类型:
 * ----- 队列
 * 先进先出, 类似于火车进站, 先进先出, 后进后出
 * 概念: 入队, 出队
 * 最底层: 队尾
 * 最外层: 队首
 */
public class QueueDemo01 {

    @Test
    public void fn01() {
        MyQueue queue = new MyQueue();
        queue.add(1);
        queue.add(2);
        queue.add(3);
        System.out.println(queue.toString());
        queue.poll();
        System.out.println(queue.toString());
    }

    private class ArrayQueue<T> {
        private int size;
        private Object[] queue;

        public ArrayQueue(int size) {
            this.size = size;
            this.queue = new Object[size];
        }

        public boolean isFull() {
            return queue.length >= size;
        }

        public void put(T t) {
            if (isFull()) {
                throw new RuntimeException("the queue has already full.");
            }

        }
    }

    private class MyQueue {

        private int[] queue;

        public MyQueue() {
            queue = new int[0];
        }

        // 入队
        public void add(int i) {
            int[] newQueue = new int[queue.length + 1];
            System.arraycopy(queue, 0, newQueue, 0, queue.length);
            newQueue[newQueue.length - 1] = i;
            queue = newQueue;
        }

        // 出队
        public void poll() {
            if (queue.length > 1) {
                int[] newQueue = new int[queue.length - 1];
                System.arraycopy(queue, 1, newQueue, 0, queue.length - 1);
                queue = newQueue;
            }
        }

        @Override
        public String toString() {
            return "MyQueue{" +
                    "blocking=" + Arrays.toString(queue) +
                    '}';
        }
    }
}
