package com.zy.tools.undefined.datastructure.tree;

import org.junit.Test;

import java.util.Objects;

/**
 * 链式二叉树 && 线索化二叉树
 * https://blog.csdn.net/UncleMing5371/article/details/54176252
 */
public class TreeDemo03 {

    @Test
    public void fn01() {
        String[] arr = {"A", "B", "C", "D", "E", "F", "G", "H"};
        // 创建二叉树
        Node<String> root = ThreadBinaryTree.createBinaryTree(arr, 0);
        ThreadBinaryTree<String> tree = new ThreadBinaryTree<>();
        // 中序线索化二叉树
        tree.midOrderThread(root);
        // 中序按后继节点遍历线索二叉树
        tree.midOrderLoopFollow(root);
        System.out.println("--------------------");
        // 中序按后继节点遍历线索二叉树
        tree.midOrderLoopPre(root);
    }

    private static class ThreadBinaryTree<E> {
        // 线索化时记录前一个节点, 即前驱节点
        private Node<E> preNode;

        /**
         * 通过数组构造一个二叉树（完全二叉树）
         *
         * @param arr
         * @param index
         * @param <E>
         * @return
         */
        static <E> Node<E> createBinaryTree(E[] arr, int index) {
            Node<E> node = null;
            if (index < arr.length) {
                node = new Node<>(arr[index]);
                node.left = createBinaryTree(arr, index * 2 + 1);
                node.right = createBinaryTree(arr, index * 2 + 2);
            }
            return node;
        }

        /**
         * 中序线索化二叉树
         *
         * @param node
         */
        void midOrderThread(Node<E> node) {
            if (Objects.isNull(node)) {
                return;
            }
            // 1.先线索化左子树
            midOrderThread(node.left);

            // 2.线索化当前节点
            // 如果左指针为空, 则将左指针指向前驱节点
            if (Objects.isNull(node.left)) {
                node.left = preNode;
                node.leftThread = true;
            }
            // 如果右指针为空, 则将前一个节点的后继节点指向当前节点
            if (Objects.nonNull(preNode) && Objects.isNull(preNode.right)) {
                preNode.right = node;
                preNode.rightThread = true;
            }
            // 每处理一个节点后, 让当前节点是下一个节点的前驱节点
            preNode = node;

            // 3.线索化右子树
            midOrderThread(node.right);
        }

        /**
         * 中序遍历线索二叉树，按照后继方式遍历（思路：找到最左子节点开始）
         * @param node
         */
        void midOrderLoopFollow(Node<E> node) {
            // 找中序遍历方式开始的节点
            while (Objects.nonNull(node) && !node.leftThread) {
                node = node.left;
            }
            while (Objects.nonNull(node)) {
                System.out.println(node.item);
                // 如果右指针是线索
                if (node.rightThread) {
                    node = node.right;
                } else {
                    // 如果右指针不是线索, 找到右子树开始的节点
                    node = node.right;
                    while (Objects.nonNull(node) && !node.leftThread) {
                        node = node.left;
                    }
                }
            }
        }

        /**
         * 中序遍历线索二叉树，按照前驱方式遍历（思路：找到最右子节点开始倒序遍历）
         * @param node
         */
        void midOrderLoopPre(Node<E> node) {
            // 找最后一个节点
            while (Objects.nonNull(node) && Objects.nonNull(node.right) && !node.rightThread) {
                node = node.right;
            }
            while (Objects.nonNull(node)) {
                System.out.println(node.item);
                // 如果左指针是线索
                if (node.leftThread) {
                    node = node.left;
                } else {
                    node = node.left;
                    while (Objects.nonNull(node.right) && !node.rightThread) {
                        node = node.right;
                    }
                }
            }
        }
    }


    private static class Node<E> {
        // 数据
        E item;
        // 左子节点
        Node<E> left;
        // 右子节点
        Node<E> right;
        // 左指针域类型  false：指向子节点、true：前驱或后继线索
        boolean leftThread = false;
        // 右指针域类型  false：指向子节点、true：前驱或后继线索
        boolean rightThread = false;

        Node(E item) {
            this.item = item;
        }
    }
}
