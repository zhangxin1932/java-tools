package com.zy.tools.undefined.datastructure.recursion;

import org.junit.Test;

/**
 * 递归能解决什么样的问题?
 * 1.各种数学问题如: 8皇后问题 , 汉诺塔, 阶乘问题, 迷宫问题, 球和篮子的问题
 * 2.各种算法中也会使用到递归，比如快排，归并排序，二分查找，分治算法等.
 * 3.将用栈解决的问题-->递归代码比较简洁
 */
public class RecursionDemo01 {

    @Test
    public void fn01() {
        System.out.println(fibonacci(5));
    }

    /**
     * 斐波那契数列
     *
     * @param n
     * @return
     */
    private int fibonacci(int n) {
        if (n == 1 || n == 2) {
            return 1;
        }
        return fibonacci(n - 1) + fibonacci(n - 2);
    }

    @Test
    public void fn02() {
        System.out.println(factorial(5));
    }

    /**
     * 求阶乘
     *
     * @param n
     * @return
     */
    private int factorial(int n) {
        if (n == 1) {
            return 1;
        }
        return n * factorial(n - 1);
    }

    /**
     * 原始迷宫, 1代表墙壁
     * 1	1	1	1	1	1	1
     * 1	2	0	0	0	0	1
     * 1	2	2	2	0	0	1
     * 1	1	1	2	0	0	1
     * 1	0	0	2	0	0	1
     * 1	0	0	2	0	0	1
     * 1	0	0	2	2	2	1
     * 1	1	1	1	1	1	1
     */
    @Test
    public void fn03() {
        // 初始化: 创建迷宫, 8行7列, value为1表示墙壁, 四周都置为1
        int[][] maze = new int[8][7];
        int rowLength = maze.length;
        int columnLength = maze[0].length;
        for (int i = 0; i < rowLength; i++) {
            maze[i][0] = 1;
            maze[i][columnLength - 1] = 1;
        }
        for (int j = 0; j < columnLength; j++) {
            maze[0][j] = 1;
            maze[rowLength - 1][j] = 1;
        }
        maze[3][1] = 1;
        maze[3][2] = 1;

        // 输出原始路径标识
        loopArr(maze);
        System.out.println("..........................................");

        // 使用递归的方式, 走迷宫
        System.out.println(setWay(maze, 1, 1));

        // 重新输出走过的路径标识
        loopArr(maze);
    }

    private void loopArr(int[][] maze) {
        for (int i = 0; i < maze.length; i++) {
            int[] rows = maze[i];
            for (int j = 0; j < rows.length; j++) {
                System.out.print(maze[i][j] + "\t");
            }
            System.out.println();
        }
    }

    /**
     * 走迷宫问题
     * 当 maze[i][j] = 0 表示该点没走过, 1 表示该点墙壁, 2 表示该点可以走, 3 表示该点已经走过但是走不通
     * 假设走迷宫的策略是: 先向下--> 直到走不通时再向右 --> 直到走不通时再向上 --> 直到走不通时再向左
     * @param maze 表示迷宫
     * @param i 出发时的横坐标
     * @param j 出发时的纵坐标
     * @return true 表示走通了
     */
    private boolean setWay(int[][] maze, int i, int j) {
        if (maze[6][5] == 2) {
            return true;
        } else {
            // 如果当前的点还没有走过
            if (maze[i][j] == 0) {
                // 按照策略 下 --> 右 --> 上 --> 左 走动
                // 先假定该点可以走通, 赋值为 2
                maze[i][j] = 2;
                if (setWay(maze, i + 1, j)) {
                    // 向下走
                    return true;
                } else if (setWay(maze, i, j + 1)) {
                    // 向右走
                    return true;
                } else if (setWay(maze, i - 1, j)) {
                    // 向上走
                    return true;
                } else if (setWay(maze, i, j - 1)) {
                    // 向左走
                    return true;
                } else {
                    // 说明该点走不通
                    maze[i][j] = 3;
                    return false;
                }
            } else {
                return false;
            }
        }
    }


    //////////////////////// 8 皇后问题开始  ////////////////////////
    private static int countQueueWay = 0;

    @Test
    public void fn04() {
        // 定义数组, 保存皇后位置的结果, 比如 arr = {0,4,7,5,2,6,1,3}
        int[] arr = new int[8];
        // 放置皇后
        putQueue(0, arr);
    }

    /**
     * 输出皇后的位置
     *
     * @param arr
     */
    private void printQueue(int[] arr) {
        countQueueWay++;
        System.out.print(countQueueWay + " >>>>>>> ");
        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i] + " ");
        }
        System.out.println();
    }

    /**
     * 当放第 n 个皇后时, 判断是否与之前的皇后冲突: 不在同一行, 同一列, 同一斜线上
     * @param n
     * @param arr
     * @return
     */
    private boolean canPutQueue(int n, int[] arr) {
        for (int i = 0; i < n; i++) {
            if (arr[i] == arr[n] || Math.abs(n - i) == Math.abs(arr[n] - arr[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * 放置第 n 个皇后
     * @param n
     * @param arr
     */
    private void putQueue(int n, int[] arr) {
        if (n == arr.length) {
            printQueue(arr);
            return;
        }
        // 依次放入皇后, 判断是否冲突
        for (int i = 0; i < arr.length; i++) {
            // 先把当前这个皇后, 放到该行的第一列
            arr[n] = i;
            // 判断放到第 i 列时, 判断是否冲突
            if (canPutQueue(n, arr)) {
                // 不冲突, 则开始放置第 n + 1 个皇后
                putQueue(n + 1, arr);
            }
            // 如果冲突, 就继续执行 arr[n] = i; 即将第 n 个皇后, 放置在本行的后一个位置
        }
    }

    //////////////////////// 8 皇后问题结束  ////////////////////////
}
