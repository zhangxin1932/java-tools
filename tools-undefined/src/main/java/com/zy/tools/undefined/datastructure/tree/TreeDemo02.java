package com.zy.tools.undefined.datastructure.tree;

import org.junit.Test;

import java.util.Objects;

/**
 * 顺序存储二叉树
 */
public class TreeDemo02 {

    @Test
    public void fn01() {
        Integer[] arr = {1, 2, 3, 4, 5, 6, 7};
        ArrBinaryTree<Integer> tree = new ArrBinaryTree<>(arr);
        tree.preOrder(0); // 1,2,4,5,3,6,7
    }

    @Test
    public void fn02() {
        Integer[] arr = {1, 2, 3, 4, 5, 6, 7};
        ArrBinaryTree<Integer> tree = new ArrBinaryTree<>(arr);
        tree.midOrder(0); // 4,2,5,1,6,3,7
    }

    @Test
    public void fn03() {
        Integer[] arr = {1, 2, 3, 4, 5, 6, 7};
        ArrBinaryTree<Integer> tree = new ArrBinaryTree<>(arr);
        tree.postOrder(0); // 4,5,2,6,7,3,1
    }

    private static class ArrBinaryTree<E> {
        transient E[] arr;

        ArrBinaryTree(E[] arr) {
            this.arr = arr;
        }

        public void preOrder(int index) {
            if (Objects.isNull(arr) || arr.length == 0) {
                return;
            }
            // 输出父节点
            System.out.println(arr[index]);
            // 输出左子树
            if ((index * 2 + 1) < arr.length) {
                preOrder(index * 2 + 1);
            }
            // 输出右子树
            if ((index * 2 + 2) < arr.length) {
                preOrder(index * 2 + 2);
            }
        }

        public void midOrder(int index) {
            if (Objects.isNull(arr) || arr.length == 0) {
                return;
            }

            // 输出左子树
            if ((index * 2 + 1) < arr.length) {
                midOrder(index * 2 + 1);
            }

            // 输出父节点
            System.out.println(arr[index]);

            // 输出右子树
            if ((index * 2 + 2) < arr.length) {
                midOrder(index * 2 + 2);
            }
        }

        public void postOrder(int index) {
            if (Objects.isNull(arr) || arr.length == 0) {
                return;
            }

            // 输出左子树
            if ((index * 2 + 1) < arr.length) {
                postOrder(index * 2 + 1);
            }

            // 输出右子树
            if ((index * 2 + 2) < arr.length) {
                postOrder(index * 2 + 2);
            }

            // 输出父节点
            System.out.println(arr[index]);
        }
    }

}
