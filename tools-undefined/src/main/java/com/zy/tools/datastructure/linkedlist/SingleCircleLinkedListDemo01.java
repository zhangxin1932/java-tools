package com.zy.tools.datastructure.linkedlist;

import com.zy.tools.datastructure.entity.Hero;
import org.junit.Test;
import org.springframework.util.Assert;

import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 环形链表解决 约瑟夫问题:
 * 设编号为1，2，… n的n个人围坐一圈，
 * 约定编号为k（1<=k<=n）的人从1开始报数，数到m 的那个人出列，它的下一位又从1开始报数，数到m的那个人又出列，
 * 依次类推，直到所有人出列为止，由此产生一个出队编号的序列。
 *
 * 提示, 用一个不带头结点的循环链表来处理Josephu 问题：
 * 先构成一个有n个结点的单循环链表（单向环形链表），
 * 然后由k结点起从1开始计数，计到m时，对应结点从链表中删除，然后再从被删除结点的下一个结点又从1开始计数，
 * 直到最后一个结点从链表中删除算法结束。
 */
public class SingleCircleLinkedListDemo01 {

    @Test
    public void fn01() {
        SingleCircleLinkedList<Hero> list = new SingleCircleLinkedList<>();
        list.add(new Hero(1, "宋江"));
        list.add(new Hero(2, "晁盖"));
        list.add(new Hero(3, "卢俊义"));
        list.add(new Hero(4, "吴用"));
        list.add(new Hero(5, "公孙胜"));
        System.out.println(list.size());
        list.loop();
    }

    private static class SingleCircleLinkedList<E> {
        transient Node<E> first;
        transient Node<E> current;
        transient int size = 0;

        public boolean add(E e) {
            Assert.notNull(e, "element cannot be null.");
            Node<E> newNode = new Node<>(e);
            if (Objects.isNull(first)) {
                first = newNode;
                current = first;
            } else {
                current.next = newNode;
                current = newNode;
            }
            current.next = first;
            size ++;
            return true;
        }

        public int size() {
            return size;
        }

        /**
         *
         * @param k 是第一次开始报数的人的位置
         * @param m 是每次从 1 开始计数时, 数到 m 个位置时, 取出数据放到队列中
         * @return
         */
        public LinkedBlockingQueue<E> Josephus (int k, int m) {
            Assert.notNull(first, "list cannot be empty.");
            Assert.isTrue(k >= 1, "k must be bigger than 1.");
            Assert.isTrue(k <= size, "k must be smaller than list'size.");
            Assert.isTrue(m >=1, "m must be positive.");
            Assert.isTrue(m <= size, "m must be smaller than list'size");

            // TODO 这里需要补充 josephus 问题的解决方案
            LinkedBlockingQueue<E> queue = new LinkedBlockingQueue<>(size);
            Node<E> current = first;
            Node<E> prev = first;
            int i = 0;
            // 将 current 移动到第一次开始报数的人的位置
            while (i < k) {
                prev = current;
                current = current.next;
                i ++;
            }

            int index = 0;
            int count = size;
            while (count >= m) {
                /*if (index == m) {
                    queue.offer(current.item);
                    prev.next = current.next;
                    current = current.next;
                    count--;
                    index = 0;
                }
                prev = current;
                current = current.next;
                index ++;*/
            }

            return queue;
        }

        public void loop() {
            Assert.notNull(first, "list cannot be empty.");
            Node<E> next = first;
            while (Objects.nonNull(next)) {
                System.out.println(next.item);
                next = next.next;
                if (Objects.equals(next, first)) {
                    break;
                }
            }
        }
    }

    private static class Node<E> {
        E item;
        Node<E> next;

        Node(E item) {
            this.item = item;
        }
    }
}
