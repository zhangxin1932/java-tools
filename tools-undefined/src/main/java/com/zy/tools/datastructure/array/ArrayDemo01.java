package com.zy.tools.datastructure.array;

import org.junit.Test;

import java.util.Arrays;

/**
 * 数据类型:
 *      ----- 数组
 */
public class ArrayDemo01 {

    private static int[] arr01 = {11, 22, 33};

    //////////////////////////////  1.创建一个数组  ////////////////////////////////
    @Test
    public void fn01() {
        // 创建数组方法1:
        int[] arr = new int[2];
        arr[0] = 1;
        arr[1] = 2;
        for (int i : arr) {
            System.out.println(i);
        }
    }

    @Test
    public void fn02() {
        // 创建数组方法2:
        int[] arr = new int[]{1, 2, 3};
        System.out.println(arr.length);
    }

    @Test
    public void fn03() {
        // 创建数组方法3:
        int[] arr = {2, 3, 4};
        System.out.println(arr[2]);
    }

    //////////////////////////////  2.复制一个数组  ////////////////////////////////
    @Test
    public void fn04() {
        int[] arr02 = new int[4];
        // 复制数组方法1:
        System.arraycopy(arr01, 0, arr02, 0, 3);
        // System.arraycopy(arr01, 0, arr02, 1, 3);
        System.out.println(Arrays.toString(arr02));
    }

    @Test
    public void fn05() {
        // 复制数组方法2:
        int[] arr02 = Arrays.copyOf(arr01, 4);
        System.out.println(Arrays.toString(arr02));
    }

    @Test
    public void fn06() {
        // 复制数组方法3:
        int[] arr02 = arr01.clone();
        System.out.println(Arrays.toString(arr02));
    }

    @Test
    public void fn07() {
        // 复制数组方法4:
        int[] arr02 = new int[4];
        for (int i = 0; i < arr01.length; i++) {
            arr02[i] = arr01[i];
        }
        Arrays.stream(arr02).forEach(value -> System.out.println(value));
    }

    /////////////////////////////////  删除一个数组中的某个元素  ////////////////////////////////////
    @Test
    public void fn08() {
        int[] arr02 = new int[2];
        for (int i = 0; i < arr01.length; i++) {
            if (i < 1) {
                arr02[i] = arr01[i];
            } else if (i > 1) {
                arr02[i - 1] = arr01[i];
            }
        }
        Arrays.stream(arr02).forEach(value -> System.out.println(value));
    }

    /////////////////////////////////  数组的面向对象思想  ////////////////////////////////////
    @Test
    public void fn09() {
        MyArray<Integer> myArray = new MyArray<>();
        myArray.add(1);
        System.out.println(myArray.toString());
    }

    /////////////////////////////////  查找一个数组中的某个元素  ////////////////////////////////////
    @Test
    public void fn10() {
        // 线性查找
        int[] arr = {3, 6, 9};
        int target = 9;
        int index = -1;
        for (int i = 0; i < arr.length; i ++) {
            if (arr[i] == target) {
                index = i;
                break;
            }
        }
        System.out.println(index);
    }

    @Test
    public void fn11() {
        // 二分查找: 需要数组是有序的
        int[] arr = {3, 6, 9};
        int target = 9;
        // int i = Arrays.binarySearch(arr, target);
        // System.out.println(i);
        int begin = 0;
        int end = arr.length -1;
        int mid = (begin + end) / 2;
        int index = -1;
        while (begin <= end) {
            if (arr[mid] == target) {
                index = mid;
                break;
            } else if (arr[mid] < target) {
                begin = mid + 1;
                mid = (begin + end) / 2;
            } else {
                end = mid - 1;
                mid = (begin + end) / 2;
            }
        }
        System.out.println(index);
    }

    /**
     * 二维数组的压缩 --> 稀疏数组
     */
    @Test
    public void fn12() {
        // step1: 创建一个二维数组 10 * 10
        // 0: 表示没有棋子, 1: 表示黑子, 2: 表示白子
        int chessArrOrigin[][] = new int[10][10];
        chessArrOrigin[1][2] = 1;
        chessArrOrigin[2][3] = 2;
        // 2.输出原始的二维数组
        System.out.println("原始二维数组为 >>>>>>>>>>>>>>>>>>>>>>>>>> ");
        for (int[] row : chessArrOrigin) {
            for (int column : row) {
                System.out.printf("%d\t", column);
            }
            System.out.println();
        }
        // 3.将原始的二维数组转为稀疏数组
        System.out.println("将原始二维数组转为稀疏数组为 >>>>>>>>>>>>>>>>>>>>>>>>>> ");
        int sum = 0;
        for (int i = 0; i < 10; i ++) {
            for (int j = 0; j < 10; j ++) {
                if (chessArrOrigin[i][j] != 0) {
                    sum++;
                }
            }
        }
        if (sum == 0) {
            return;
        }
        int chessArrSparse[][] = new int[sum + 1][3];
        // 给稀疏数组赋值
        // 稀疏数组的第一行: 原始数组行数 原始数组列数 原始数组有效数据数
        chessArrSparse[0][0] = 10;
        chessArrSparse[0][1] = 10;
        chessArrSparse[0][2] = sum;
        // 遍历原始二维数组, 将有效数据数放到稀疏数组中去
        int count = 0;
        for (int i = 0; i < 10; i ++) {
            for (int j = 0; j < 10; j ++) {
                if (chessArrOrigin[i][j] != 0) {
                    count ++;
                    chessArrSparse[count][0] =i;
                    chessArrSparse[count][1] =j;
                    chessArrSparse[count][2] =chessArrOrigin[i][j];
                }
            }
        }
        for (int[] row : chessArrSparse) {
            for (int column : row) {
                System.out.printf("%d\t", column);
            }
            System.out.println();
        }
        // 4.将稀疏数组转为原始的二维数组
        System.out.println("将稀疏数组转为原始二维数组为 >>>>>>>>>>>>>>>>>>>>>>>>>> ");
        int[][] chessArrSparse2Origin = new int[chessArrSparse[0][0]][chessArrSparse[0][1]];
        for (int i = 1; i < chessArrSparse.length; i ++) {
            int row = chessArrSparse[i][0];
            int column = chessArrSparse[i][1];
            int value = chessArrSparse[i][2];
            chessArrSparse2Origin[row][column] = value;
        }
        for (int[] row : chessArrSparse2Origin) {
            for (int column : row) {
                System.out.printf("%d\t", column);
            }
            System.out.println();
        }
    }


    /////////////////////////////////  数组的面向对象思想  ////////////////////////////////////
    private class MyArray<T> {
        private Object[] ele;

        public MyArray() {
            ele = new Object[0];
        }

        public int size() {
            return ele.length;
        }

        public void add(T e) {
            Object[] ele01 = new Object[ele.length + 1];
            System.arraycopy(ele, 0, ele, 0, ele.length);
            ele01[ele01.length - 1] = e;
            ele = ele01;
        }

        @Override
        public String toString() {
            return "MyArray{" +
                    "ele=" + Arrays.toString(ele) +
                    '}';
        }
    }

}
