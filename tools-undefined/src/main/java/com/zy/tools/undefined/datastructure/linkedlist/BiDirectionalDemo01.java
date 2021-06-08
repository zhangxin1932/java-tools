package com.zy.tools.undefined.datastructure.linkedlist;


import com.zy.tools.undefined.datastructure.entity.Hero;
import org.junit.Test;
import org.springframework.util.Assert;

import java.util.Objects;

/**
 * 双向链表 Demo
 */
public class BiDirectionalDemo01 {

    @Test
    public void fn01(){
        BiDirectionalList<Hero> list = new BiDirectionalList<>();
        list.add(new Hero(1, "宋江"));
        list.add(new Hero(2, "晁盖"));
        list.add(new Hero(3, "卢俊义"));
        System.out.println(list.size);
        System.out.println(list.get(0));
        list.loop();
        System.out.println("--------------------");
        list.remove(0);
        list.remove(0);
        list.remove(0);
        list.loop();
    }

    private static class BiDirectionalList<E> {
        transient int size = 0;
        transient int modCount;
        transient Node<E> first;
        transient Node<E> last;

        /**
         * 向链表尾部添加元素
         * @param e
         * @return
         */
        public boolean add(E e) {
            final Node<E> l = last;
            final Node<E> newNode = new Node<>(l, e, null);
            last = newNode;
            if (l == null) {
                first = newNode;
            } else {
                l.next = newNode;
            }
            size ++;
            modCount ++;
            return true;
        }

        /**
         * 根据下标 删除链表中的元素
         * @param index
         * @return
         */
        public boolean remove(int index) {
            Assert.notNull(first, "list must not be null");
            Assert.isTrue(size > index, "array out of index bound");
            Assert.isTrue(index >= 0, "array out of index bound");

            Node<E> item = first;
            int count = 0;
            boolean flag = false;
            while (Objects.nonNull(item)) {
                if (count == index) {
                    flag = true;
                    break;
                }
                count ++;
                item = item.next;
            }

            Assert.isTrue(flag, "cannot find element in the list");
            Node<E> prev = item.prev;
            Node<E> next = item.next;
            if (Objects.isNull(prev)) {
                first = next;
            } else {
                prev.next = next;
                item.prev = null;
            }
            if (Objects.isNull(next)) {
                last = prev;
            } else {
                next.prev = prev;
                item.next = null;
            }
            size --;
            modCount ++;
            return true;
        }

        /**
         * 获取列表中下标为 index 的元素
         * @param index
         * @return
         */
        public E get(int index) {
            Assert.notNull(first, "list must not be null");
            Assert.isTrue(index >= 0, "array out of index bound");
            Assert.isTrue(size > index, "array out of index bound");
            Node<E> next = first;
            int count = 0;
            while (Objects.nonNull(next)) {
                if (count == index) {
                    break;
                }
                count ++;
                next = next.next;
            }
            return next.item;
        }

        /**
         * 遍历链表
         */
        public void loop() {
            Node<E> next = first;
            while (Objects.nonNull(next)) {
                System.out.println(next.item);
                next = next.next;
            }
        }

        /**
         * 链表中数据的数量
         * @return
         */
        public int size() {
            return size;
        }

    }

    private static class Node<E> {
        E item;
        private Node<E> prev;
        private Node<E> next;

        Node(Node<E> prev, E e, Node<E> next) {
            this.prev = prev;
            this.item = e;
            this.next = next;
        }
    }
}
