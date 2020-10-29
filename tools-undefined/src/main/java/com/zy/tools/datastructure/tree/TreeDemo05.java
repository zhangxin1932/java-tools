package com.zy.tools.datastructure.tree;

import org.junit.Test;

import java.util.Objects;

/**
 * AVL 树:
 * 核心:
 * 1.树的高度求解
 * 2.当右子树高度 - 左子树高度 > 1 时, 左旋
 * 3.当左子树高度 - 右子树高度 > 1 时, 右旋
 */
public class TreeDemo05 {

    /**
     *                  4
     *
     *          3               6
     *
     *                      5       7
     *
     *                                      8
     */
    private static Integer[] arrLeft = {4, 3, 6, 5, 7, 8};
    /**
     *                              10
     *
     *                      8               12
     *
     *              7             9
     *
     *       6
     */
    private static Integer[] arrRight = {10,12,8,9,7,6};
    private static Integer[] arrBoth = {2,1,6,5,7,3};
    // private static Integer[] arrBoth = {10,11,7,6,8,9};
    // private static Integer[] arrBoth = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16};
    private static AVLTree<Integer> avlTreeLeft = new AVLTree<>();
    private static AVLTree<Integer> avlTreeRight = new AVLTree<>();
    private static AVLTree<Integer> avlTreeBoth = new AVLTree<>();
    static {
        for (Integer i : arrLeft) {
            avlTreeLeft.insert(i);
        }
        for (Integer i : arrRight) {
            avlTreeRight.insert(i);
        }
        for (Integer i : arrBoth) {
            avlTreeBoth.insert(i);
        }
    }

    @Test
    public void fn01() {
        // 测试左旋
        System.out.println("avl 树的高度: " + avlTreeLeft.getRoot().height());
        System.out.println("avl 左子树的高度: " + avlTreeLeft.getRoot().leftTreeHeight());
        System.out.println("avl 右子树的高度: " + avlTreeLeft.getRoot().rightTreeHeight());
        avlTreeLeft.midOrderLoopFromRoot();
    }

    @Test
    public void fn02() {
        // 测试右旋
        System.out.println("avl 树的高度: " + avlTreeRight.getRoot().height());
        System.out.println("avl 左子树的高度: " + avlTreeRight.getRoot().leftTreeHeight());
        System.out.println("avl 右子树的高度: " + avlTreeRight.getRoot().rightTreeHeight());
        avlTreeRight.midOrderLoopFromRoot();
    }

    @Test
    public void fn03() {
        // 测试双旋
        System.out.println("avl 树的高度: " + avlTreeBoth.getRoot().height());
        System.out.println("avl 左子树的高度: " + avlTreeBoth.getRoot().leftTreeHeight());
        System.out.println("avl 右子树的高度: " + avlTreeBoth.getRoot().rightTreeHeight());
        avlTreeBoth.midOrderLoopFromRoot();
    }

    private static class AVLTree<E> {
        transient Node<E> root;
        transient int size;

        public Node<E> getRoot() {
            return this.root;
        }

        public int getSize() {
            return size;
        }

        public boolean insert(E item) {
            if (Objects.isNull(item)) {
                throw new IllegalArgumentException("param cannot be null");
            }
            Node<E> eNode = new Node<>(item);
            if (Objects.isNull(this.root)) {
                this.root = eNode;
                return true;
            }

            Node<E> current = this.root;
            Node<E> parent;
            boolean flag;
            while (true) {
                parent = current;
                if (item.hashCode() < current.item.hashCode()) {
                    current = current.left;
                    if (Objects.isNull(current)) {
                        parent.left = eNode;
                        eNode.parent = parent;
                        size++;
                        flag =  true;
                        break;
                    }
                } else if (item.hashCode() > current.item.hashCode()) {
                    current = current.right;
                    if (Objects.isNull(current)) {
                        parent.right = eNode;
                        eNode.parent = parent;
                        size++;
                        flag = true;
                        break;
                    }
                } else {
                    flag = false;
                    break;
                }
            }
            if (!flag) {
                return false;
            }

            // 如果添加完节点后, 右子树的高度 - 左子树的高度 > 1, 进行左旋
            if (this.root.rightTreeHeight() - this.root.leftTreeHeight() > 1) {
                if (this.root.right != null && this.root.right.leftTreeHeight() > this.root.right.rightTreeHeight()) {
                    this.root.right.rightRotate();
                }
                this.root.leftRotate();
                return true;
            }

            // 如果添加完节点后, 左子树的高度 - 右子树的高度 > 1, 进行右旋
            if (this.root.leftTreeHeight() - this.root.rightTreeHeight() > 1) {
                if (this.root.left != null && this.root.left.rightTreeHeight() > this.root.left.leftTreeHeight()) {
                    this.root.left.leftRotate();
                }
                this.root.rightRotate();
            }

            return true;
        }

        public void midOrderLoopFromRoot() {
            if (Objects.isNull(this.root)) {
                return;
            }
            midOrderLoop(this.root);
        }

        public void midOrderLoop(Node<E> node) {
            if (Objects.isNull(node)) {
                return;
            }
            midOrderLoop(node.left);
            System.out.println(node.item);
            midOrderLoop(node.right);
        }
    }


    private static class Node<E> {
        E item;
        Node<E> left;
        Node<E> right;
        Node<E> parent;
        boolean delete = false;

        public Node(E item) {
            this.item = item;
        }

        /**
         * 当前节点的高度
         * @return
         */
        public int height() {
            return Math.max(this.left == null ? 0 : this.left.height(), this.right == null ? 0 : this.right.height()) + 1;
        }

        /**
         * 当前节点右子树的高度
         * @return
         */
        public int rightTreeHeight() {
            return this.right == null ? 0 : this.right.height();
        }

        /**
         * 当前节点左子树的高度
         * @return
         */
        public int leftTreeHeight() {
            return this.left == null ? 0 : this.left.height();
        }

        /**
         * 当右子树的高度 - 左子树高度 > 1 时, 左旋
         */
        public void leftRotate() {
            // 1.创建一个新的节点, 该节点的 值 等于当前根节点的值
            Node<E> newNode = new Node<>(item);
            // 2.把新节点的左子树设置为当前节点的左子树
            newNode.left = this.left;
            // 3.把新节点的右子树设置为当前节点的右子树的左子树
            newNode.right = this.right.left;
            // 4.把当前节点的值替换为当前节点右子节点的值
            this.item = this.right.item;
            // 5.把当前节点的右子树设置为当前节点的右子树的右子树
            this.right = this.right.right;
            // 6.把当前节点的左子树设置为新节点
            this.left = newNode;
        }

        /**
         * 当左子树的高度 - 右子树的高度 > 1 时, 右旋
         */
        public void rightRotate() {
            // 1.创建一个新的节点, 该节点的值等于当前根节点的值
            Node<E> newNode = new Node<>(item);
            // 2.把新节点的右子树设置为当前节点的右子树
            newNode.right = this.right;
            // 3.把新节点的左子树设置为当前节点的左子树的右子树
            newNode.left = this.left.right;
            // 4.把当前节点的值替换为当前节点的左子节点的值
            this.item = this.left.item;
            // 5.把当前节点的左子树设置为当前节点的左子树的左子树
            this.left = this.left.left;
            // 6.把当前节点的右子树设置为新节点
            this.right = newNode;
        }
    }
}
