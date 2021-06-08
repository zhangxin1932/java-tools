package com.zy.tools.undefined.datastructure.linkedlist;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.springframework.util.Assert;
import java.util.Objects;

/**
 * 单向链表 Demo
 */
/**
 * 使用带 Head 的单向链表实现水浒英雄排行榜管理
 * 1.完成对英雄人物的增删改查工作
 * 2.方式1:添加英雄时, 直接添加到链表的尾部
 * 3.方式2:添加英雄时, 根据英雄的排名将英雄插入到指定的位置
 */
public class SingleLinkedListDemo01 {

    @Test
    public void fn01() {
        SingleLinkedList list = new SingleLinkedList();
        list.add(new Node(2, "晁盖"));
        list.add(new Node(1, "宋江"));
        list.add(new Node(3, "卢俊义"));
        list.loop();
        System.out.println("-------------");
        // 其实这里面 new 的对象和上文中的内存地址不同, 但由于 Node 重写了 hashCode & equals 方法, 因此, 二者是相等的, 此处才可以移除.
        list.remove(new Node(3, "卢俊义"));
        list.loop();
        System.out.println("size: " + list.size());
    }

    @Test
    public void fn02() {
        SingleLinkedList list = new SingleLinkedList();
        list.addAsc(new Node(3, "卢俊义"));
        list.addAsc(new Node(2, "晁盖"));
        list.addAsc(new Node(1, "宋江"));
        list.loop();
        System.out.println("-----------");
        System.out.println(list.get(2));
        System.out.println("-----------");
        SingleLinkedList reversed = list.reversed();
        reversed.loop();
    }

    private static class SingleLinkedList {
        // 初始化一个头节点
        transient Node head = new Node(0, null);
        // 尾部节点
        transient Node tail;
        transient int size = 0;

        /**
         * 当不需要排序时, 找到该链表的尾部节点, 将尾部节点的 next 域指向该节点
         *
         * @param node
         */
        public boolean add(Node node) {
            checkNodeAttr(node);
            if (Objects.isNull(tail)) {
                head.setNext(node);
            } else {
                tail.setNext(node);
            }
            tail = node;
            size ++;
            return true;
        }

        /**
         * 升序插入
         * @param node
         * @return
         */
        public boolean addAsc(Node node) {
            checkNodeAttr(node);

            Node next = head;
            boolean flag = true;
            while (Objects.nonNull(next.getNext())) {
                if (next.getNext().getNo() > node.getNo()) {
                    // 在 next 的后面插入
                    break;
                } else if (next.getNext().getNo() == node.getNo()) {
                    // 节点已存在
                    flag = false;
                    break;
                } else {
                    // 指针后移, 继续查找
                    next = next.getNext();
                }
            }
            Assert.isTrue(flag, "该节点已存在:" + node);
            node.setNext(next.getNext());
            next.setNext(node);
            size ++;
            return true;
        }

        /**
         * 删除节点
         * @param node
         * @return
         */
        public boolean remove(Node node) {
            checkNodeAttr(node);
            Assert.notNull(tail, "the node is not contained in the list.");
            Node next = head;
            boolean flag = false;
            while (Objects.nonNull(next.getNext())) {
                if (Objects.equals(next.getNext(), node)) {
                    flag = true;
                    break;
                } else {
                    next = next.getNext();
                }
            }
            Assert.isTrue(flag, "该节点不存在:" + node);
            next.setNext(next.getNext().getNext());
            size --;
            return true;
        }

        /**
         * 链表的遍历
         */
        public void loop() {
            Node next = head.getNext();
            while (Objects.nonNull(next)) {
                System.out.println(next);
                next = next.getNext();
            }
        }

        /**
         * 获取链表的 size
         * @return
         */
        public int size() {
            return size;
        }

        /**
         * 获取链表中 下标 为 index 的元素
         * @param index
         * @return
         */
        public Node get(int index) {
            Assert.isTrue(size > index , "array out of index bound");
            Assert.isTrue(index >= 0, "array out of index bound");
            int i = 0;
            Node next = head.getNext();
            while (Objects.nonNull(next)) {
                if (i == index) {
                    break;
                }
                i ++;
                next = next.getNext();
            }
            return next;
        }

        /**
         * 链表反转
         * @return
         */
        public SingleLinkedList reversed() {
            Assert.isTrue(size > 0, "list must not be empty.");
            int i = size - 1;
            SingleLinkedList list = new SingleLinkedList();
            while (i >= 0) {
                Node node = get(i);
                if (i == 0) {
                    node.setNext(null);
                }
                list.add(node);
                i --;
            }
            return list;
        }

        private void checkNodeAttr(Node node) {
            Assert.notNull(node, "node cannot be null.");
            Assert.isTrue(node.getNo() > 0, "node's no must be positive.");
            Assert.isTrue(StringUtils.isNotBlank(node.getName()), "node's name cannot be empty.");
        }
    }

    @Setter
    @Getter
    @EqualsAndHashCode
    private static class Node {
        private static final String SIGN = "\"";
        /**
         * 英雄位置
         */
        private int no;
        /**
         * 英雄名称
         */
        private String name;
        /**
         * 下一个英雄
         */
        private Node next;

        Node(int no, String name) {
            this.no = no;
            this.name = name;
        }

        @Override
        public String toString() {
            String s1 = String.format("{%sno%s:%s%d%s, %sname%s:%s%s%s}", SIGN, SIGN, SIGN, no, SIGN, SIGN, SIGN, SIGN, name, SIGN);
            String s2;
            if (Objects.isNull(next)) {
                s2 = String.format("{%sno%s:%s%d%s, %sname%s:%s%s%s}", SIGN, SIGN, SIGN, -1, SIGN, SIGN, SIGN, SIGN, "-", SIGN);
            } else {
                s2 = String.format("{%sno%s:%s%d%s, %sname%s:%s%s%s}", SIGN, SIGN, SIGN, next.getNo(), SIGN, SIGN, SIGN, SIGN, next.getName(), SIGN);
            }
            return String.format("[%s,%s]", s1, s2);
        }
    }
}
