package com.zy.tools.datastructure.tree;

import org.junit.Test;

import java.util.Objects;

/**
 * 二叉排序树
 * <p>
 * https://www.jb51.net/article/77278.htm
 */
public class TreeDemo04 {

    /**
     *                  7
     *
     *          3               10
     *
     *      1       5       9       12
     *
     *          2       8
     */
    private static int[] arr = {7, 3, 10, 12, 5, 1, 9, 2, 8};
    private static BinarySearchTree<Integer> bst = new BinarySearchTree<>();
    static {
        for (int i : arr) {
            bst.insert(i);
        }
    }

    @Test
    public void fn01() {
        System.out.println("\n1.中序遍历 --------------");
        bst.midOrderLoop();
        System.out.println("\n2.删除叶子节点 5 --------------");
        bst.delete(5);
        bst.midOrderLoop();
    }

    @Test
    public void fn02() {
        System.out.println("\n3.删除只有一颗右子树的节点 1 --------------");
        bst.delete(1);
        bst.midOrderLoop();
        System.out.println("\n4.删除只有一颗左子树的节点 9 --------------");
        bst.delete(8);
        bst.midOrderLoop();
    }

    @Test
    public void fn03() {
        System.out.println("\n5.删除有两个子树的节点");
        bst.delete(7);
        bst.delete(3);
        bst.delete(12);
        bst.delete(5);
        bst.delete(9);
        bst.delete(2);
        bst.delete(8);
        bst.delete(1);
        bst.delete(10);
        bst.midOrderLoop();
    }


    private static class BinarySearchTree<E> {

        transient Node<E> root;
        transient int size;

        public int getSize() {
            return size;
        }

        public boolean insert(E item) {
            if (Objects.isNull(item)) {
                throw new RuntimeException("node cannot be null");
            }
            // 比较传入的节点的值和当前子树的根节点的值的大小
            if (Objects.isNull(root)) {
                this.root = new Node<>(item);
                return true;
            }
            Node<E> node = new Node<>(item);
            Node<E> currentNode = root;
            Node<E> parentNode;

            while (true) {
                parentNode = currentNode;
                // 如果插入的数据比父节点小
                if (currentNode.item.hashCode() > item.hashCode()) {
                    currentNode = currentNode.left;
                    // 如果当前父节点的左子节点为空
                    if (Objects.isNull(currentNode)) {
                        parentNode.left = node;
                        node.parent = parentNode;
                        size++;
                        return true;
                    }
                } else if (currentNode.item.hashCode() < item.hashCode()) {
                    currentNode = currentNode.right;
                    // 如果当前父节点的右子节点为空
                    if (Objects.isNull(currentNode)) {
                        parentNode.right = node;
                        node.parent = parentNode;
                        size++;
                        return true;
                    }
                } else {
                    // 简化处理: 如果 hash 冲突了, 这里就不加入这颗树了
                    return false;
                }
            }
        }

        public Node<E> findNode(E item) {
            if (Objects.isNull(item) || Objects.isNull(root)) {
                return null;
            }
            Node<E> currentNode = root;
            while (Objects.nonNull(currentNode)) {
                if (currentNode.item.hashCode() > item.hashCode()) {
                    currentNode = currentNode.left;
                } else if (currentNode.item.hashCode() < item.hashCode()) {
                    currentNode = currentNode.right;
                } else {
                    break;
                }
            }
            return currentNode;
        }

        /**
         * 第一种删除节点的方案, 真实的删除节点
         *
         * @param item
         * @return
         */
        public boolean delete(E item) {
            // 1.找到该节点
            Node<E> node = findNode(item);
            if (Objects.isNull(node)) {
                return false;
            }
            // 2.根据情况分类讨论
            Node<E> parent = node.parent;
            // 2.1 如果 node 的父节点不为空
            if (Objects.nonNull(parent)) {
                // 2.1.1 如果 node 的左子节点和右子节点同时为空
                if (Objects.isNull(node.left) && Objects.isNull(node.right)) {
                    // 判断 node 是 parent 的左子节点还是右子节点
                    if (Objects.nonNull(parent.right) && Objects.equals(parent.right.item, node.item)) {
                        parent.right = null;
                    } else {
                        parent.left = null;
                    }
                } else if (Objects.isNull(node.left)) {
                    // 2.1.2 如果 node 的左子节点为空, 右子节点不空
                    // 判断 node 是 parent 的左子节点还是右子节点
                    if (Objects.nonNull(parent.right) && Objects.equals(parent.right.item, node.item)) {
                        parent.right = node.right;
                        node.right.parent = parent;
                    } else {
                        parent.left = node.right;
                        node.right.parent = parent;
                    }
                } else if (Objects.isNull(node.right)) {
                    // 2.1.3 如果 node 的右子节点为空, 左子节点不空
                    // 判断 node 是 parent 的左子节点还是右子节点
                    if (Objects.nonNull(parent.right) && Objects.equals(parent.right.item, node.item)) {
                        parent.right = node.left;
                        node.left.parent = parent;
                    } else {
                        parent.left = node.left;
                        node.left.parent = parent;
                    }
                } else {
                    // 2.1.4 如果 node 的左右子节点都不为空: 结合图形来理解更好
                    // 方案1: 先删除该节点 右子树中子孙节点中最小值 并取得该最小值, 然后将该节点的 item 指向刚才删除节点的 item
                    // 方案2: 先删除该节点 左子树中子孙节点中最大值 并取得该最大值, 然后将该节点的 item 指向刚才删除节点的 item
                    Node<E> minSon = deleteRightNodeMinSon(node.right);
                    node.item = minSon.item;
                }
            } else {
                // 2.2 如果 node 的父节点为空
                // 2.2.1 如果 node 的左子节点和右子节点同时为空
                if (Objects.isNull(node.left) && Objects.isNull(node.right)) {
                    this.root = null;
                } else if (Objects.isNull(node.left)) {
                    // 2.2.2 如果 node 的左子节点为空, 右子节点不空
                    this.root = node.right;
                    this.root.right = null;
                    this.root.parent = null;
                } else if (Objects.isNull(node.right)) {
                    // 2.2.3 如果 node 的右子节点为空, 左子节点不空
                    this.root = node.left;
                    this.root.left = null;
                    this.root.parent = null;
                } else {
                    // 2.2.4 如果 node 的左右子节点都不为空
                    Node<E> minSon = deleteRightNodeMinSon(node.right);
                    this.root.item = minSon.item;
                    this.root.parent = null;
                }
            }
            return true;
        }

        private Node<E> deleteRightNodeMinSon(Node<E> rightNode) {
            Node<E> target = rightNode;
            while (Objects.nonNull(target.left)) {
                target = target.left;
            }
            delete(target.item);
            return target;
        }

        /**
         * 不是真实的删除, 而是给该节点一个删除标识, 该节点仍然停留在该位置
         *
         * @param item
         * @return
         */
        public boolean deleteSoftly(E item) {
            // 1.找到该节点
            Node<E> node = findNode(item);
            if (Objects.isNull(node) || node.delete) {
                return false;
            }
            node.delete = true;
            return true;
        }

        public void midOrderLoop() {
            if (Objects.isNull(root)) {
                return;
            }
            midOrderLoopRecursive(root);
        }

        private void midOrderLoopRecursive(Node<E> node) {
            if (Objects.isNull(node)) {
                return;
            }
            if (Objects.nonNull(node.left)) {
                midOrderLoopRecursive(node.left);
            }
            System.out.print(node.item + "\t");
            if (Objects.nonNull(node.right)) {
                midOrderLoopRecursive(node.right);
            }
        }
    }

    private static class Node<E> {
        E item;
        Node<E> left;
        Node<E> right;
        Node<E> parent;
        boolean delete = false;

        Node(E item) {
            this.item = item;
        }

    }
}
