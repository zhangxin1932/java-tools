package com.zy.tools.undefined.datastructure.tree;

import org.junit.Test;

import java.util.Objects;
import java.util.Stack;

/**
 * 二叉树的 前序, 中序, 后续遍历/查找
 * 二叉搜索树最常用的是中序遍历。
 * >> 前序遍历:根节点——》左子树——》右子树
 * >> 中序遍历:左子树——》根节点——》右子树
 * >> 后序遍历:左子树——》右子树——》根节点
 * https://blog.csdn.net/coder__666/article/details/80349039
 *
 * https://blog.csdn.net/weixin_42030357/article/details/95313110
 */
public class TreeDemo01 {

    private static TreeNode<Integer>[] tree;

    static {
        tree = new TreeNode[10];

        for (int i = 0; i < 10; i++) {
            tree[i] = new TreeNode<>(i);
        }

        for (int i = 0; i < 10; i++) {
            if (i * 2 + 1 < 10) {
                tree[i].left = tree[i * 2 + 1];
            }
            if (i * 2 + 2 < 10) {
                tree[i].right = tree[i * 2 + 2];
            }
        }
    }

    @Test
    public void fn01() {
        // preOrderLoopRecursive(tree[0]);
        preOrderLoop(tree[0]);
    }

    @Test
    public void fn02() {
        // midOrderLoopRecursive(tree[0]);
        midOrderLoop(tree[0]);
    }

    @Test
    public void fn03() {
        // postOrderLoopRecursive(tree[0]);
        postOrderLoop(tree[0]);
    }

    @Test
    public void fn04() {
        System.out.println(preOrderSearch(3, tree[0]));
    }

    @Test
    public void fn05() {
        System.out.println(midOrderSearch(3, tree[0]));
    }

    @Test
    public void fn06() {
        System.out.println(postOrderSearch(3, tree[0]));
    }

    //////////////////////// 二叉树的前序/中序/后序遍历 ////////////////////////

    /**
     * 前序遍历一: 递归遍历
     *
     * @param treeNode
     * @param <E>
     */
    public static <E> void preOrderLoopRecursive(TreeNode<E> treeNode) {
        // step1: 输出父节点
        if (Objects.isNull(treeNode)) {
            return;
        }
        System.out.println(treeNode.item);

        // step2: 如果左子树不为空, 则遍历左子树
        TreeNode<E> leftTree = treeNode.left;
        if (Objects.nonNull(leftTree)) {
            preOrderLoopRecursive(leftTree);
        }

        // step3: 如果右子树不为空, 则遍历右子树
        TreeNode<E> rightTree = treeNode.right;
        if (Objects.nonNull(rightTree)) {
            preOrderLoopRecursive(rightTree);
        }
    }

    /**
     * 前序遍历二: 迭代遍历
     *
     * @param treeNode
     * @param <E>
     */
    public static <E> void preOrderLoop(TreeNode<E> treeNode) {
        Stack<TreeNode<E>> stack = new Stack<>();
        while (Objects.nonNull(treeNode) || !stack.empty()) {
            while (Objects.nonNull(treeNode)) {
                System.out.println(treeNode.item);
                stack.push(treeNode);
                treeNode = treeNode.left;
            }
            if (!stack.empty()) {
                treeNode = stack.pop();
                treeNode = treeNode.right;
            }
        }
    }

    /**
     * 中序遍历一: 中序递归遍历
     *
     * @param treeNode
     * @param <E>
     */
    public static <E> void midOrderLoopRecursive(TreeNode<E> treeNode) {
        if (Objects.isNull(treeNode)) {
            return;
        }
        // step1: 如果左子树不为空, 则遍历左子树
        midOrderLoopRecursive(treeNode.left);
        // step2: 输出父节点
        System.out.println(treeNode.item);
        // step3: 如果右子树不为空, 则遍历右子树
        midOrderLoopRecursive(treeNode.right);
    }

    /**
     * 中序遍历二: 中序迭代遍历
     *
     * @param treeNode
     * @param <E>
     */
    public static <E> void midOrderLoop(TreeNode<E> treeNode) {
        Stack<TreeNode<E>> stack = new Stack<>();
        while (Objects.nonNull(treeNode) || !stack.empty()) {
            while (Objects.nonNull(treeNode)) {
                stack.push(treeNode);
                treeNode = treeNode.left;
            }
            if (!stack.empty()) {
                treeNode = stack.pop();
                System.out.println(treeNode.item);
                treeNode = treeNode.right;
            }
        }
    }

    /**
     * 后续遍历一: 后序递归遍历
     *
     * @param treeNode
     * @param <E>
     */
    public static <E> void postOrderLoopRecursive(TreeNode<E> treeNode) {
        if (Objects.isNull(treeNode)) {
            return;
        }
        // step1: 如果左子树不为空, 则遍历左子树
        postOrderLoopRecursive(treeNode.left);
        // step2: 如果右子树不为空, 则遍历右子树
        postOrderLoopRecursive(treeNode.right);
        // step3: 输出父节点
        System.out.println(treeNode.item);
    }

    /**
     * 后续遍历二: 后序迭代遍历
     *
     * @param treeNode
     * @param <E>
     */
    public static <E> void postOrderLoop(TreeNode<E> treeNode) {
        // 在辅助栈里表示左节点
        int left = 1;
        // 在辅助栈里表示右节点
        int right = 2;
        Stack<TreeNode<E>> stack = new Stack<>();
        // 辅助栈，用来判断子节点返回父节点时处于左节点还是右节点。
        Stack<Integer> viceStack = new Stack<>();

        while (Objects.nonNull(treeNode) || !stack.empty()) {
            // 将节点压入栈1，并在栈2将节点标记为左节点
            while (Objects.nonNull(treeNode)) {
                stack.push(treeNode);
                viceStack.push(left);
                treeNode = treeNode.left;
            }
            // 如果是从右子节点返回父节点，则任务完成，将两个栈的栈顶弹出
            while (!stack.empty() && viceStack.peek() == right) {
                viceStack.pop();
                System.out.println(stack.pop().item);
            }
            // 如果是从左子节点返回父节点，则将标记改为右子节点
            if (!stack.empty() && viceStack.peek() == left) {
                viceStack.pop();
                viceStack.push(right);
                treeNode = stack.peek().right;
            }
        }
    }

    //////////////////////// 二叉树的前序/中序/后序查找 ////////////////////////

    /**
     * 前序查找
     * @param e
     * @param treeNode
     * @param <E>
     * @return
     */
    public static <E> E preOrderSearch(E e, TreeNode<E> treeNode) {
        if (Objects.isNull(treeNode)) {
            return null;
        }
        if (Objects.equals(e, treeNode.item)) {
            return treeNode.item;
        }

        E item = null;
        if (Objects.nonNull(treeNode.left)) {
            item = preOrderSearch(e, treeNode.left);
        }
        if (Objects.nonNull(item)) {
            return item;
        }
        if (Objects.nonNull(treeNode.right)) {
            item = preOrderSearch(e, treeNode.right);
        }
        return item;
    }

    /**
     * 中序查找
     * @param e
     * @param treeNode
     * @param <E>
     * @return
     */
    public static <E> E midOrderSearch(E e, TreeNode<E> treeNode) {
        if (Objects.isNull(treeNode)) {
            return null;
        }

        E item = null;
        if (Objects.nonNull(treeNode.left)) {
            item = midOrderSearch(e, treeNode.left);
        }
        if (Objects.nonNull(item)) {
            return item;
        }

        if (Objects.equals(e, treeNode.item)) {
            return treeNode.item;
        }

        if (Objects.nonNull(treeNode.right)) {
            item = midOrderSearch(e, treeNode.right);
        }
        return item;
    }

    /**
     * 后序查找
     * @param e
     * @param treeNode
     * @param <E>
     * @return
     */
    public static <E> E postOrderSearch(E e, TreeNode<E> treeNode) {
        if (Objects.isNull(treeNode)) {
            return null;
        }

        E item = null;
        if (Objects.nonNull(treeNode.left)) {
            item = postOrderSearch(e, treeNode.left);
        }
        if (Objects.nonNull(item)) {
            return item;
        }
        if (Objects.nonNull(treeNode.right)) {
            item = postOrderSearch(e, treeNode.right);
        }
        if (Objects.nonNull(item)) {
            return item;
        }
        if (Objects.equals(e, treeNode.item)) {
            item = treeNode.item;
        }
        return item;
    }

    private static class TreeNode<E> {
        E item;
        TreeNode<E> left;
        TreeNode<E> right;

        TreeNode(E item) {
            this.item = item;
        }
    }
}
