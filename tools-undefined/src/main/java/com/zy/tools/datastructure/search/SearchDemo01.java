package com.zy.tools.datastructure.search;

import org.junit.Test;

public class SearchDemo01 {

    ////////////////////////////// 1.线性查找 //////////////////////////////
    @Test
    public void fn01() {
        int[] arr = {12, -3, 5, 102, 20, 7};
        System.out.println(sequenceSearch(arr, 6));
    }

    /**
     * 查找算法一: 线性查找, 即遍历数组即可 (注意: 这里只返回一个)
     *
     * @param arr
     * @param v
     * @return
     */
    public static int sequenceSearch(int[] arr, int v) {
        for (int i = 0; i < arr.length; i++) {
            if (v == arr[i]) {
                return i;
            }
        }
        return -1;
    }

    ////////////////////////////// 2.二分查找 //////////////////////////////
    @Test
    public void fn02() {
        // int[] arr = {1, 2, 3, 4, 5, 6, 7, 8};
        int[] arr = {8, 7, 6, 4, 3, 2, 1};
        System.out.println(binarySearch(arr, 5, 0, arr.length - 1));
    }

    /**
     * 查找算法二: 二分查找, 要求数组必须是有序的, 这里如果有多个目标值也是只返回了一个下标
     *
     * @param arr
     * @param v
     * @param start
     * @param end
     * @return
     */
    public static int binarySearch(int[] arr, int v, int start, int end) {
        if (v == arr[start]) {
            return start;
        }
        if (v == arr[end]) {
            return end;
        }
        if (start > end) {
            return -1;
        }
        int mid = (start + end) >> 1;
        if (v > arr[start] && v < arr[end]) {
            if (v == arr[mid]) {
                return mid;
            } else if (v > arr[mid]) {
                start = mid + 1;
            } else {
                end = mid - 1;
            }
            return binarySearch(arr, v, start, end);
        } else if (v < arr[start] && v > arr[end]) {
            if (v == arr[mid]) {
                return mid;
            } else if (v > arr[mid]) {
                end = mid - 1;
            } else {
                start = mid + 1;
            }
            return binarySearch(arr, v, start, end);
        } else {
            return -1;
        }
    }

    ////////////////////////////// 3.插值查找 //////////////////////////////
    @Test
    public void fn03() {
        // int[] arr = {1, 2, 3, 4, 5, 6, 7, 8};
        int[] arr = {8, 7, 6, 4, 3, 2, 1};
        System.out.println(insertSearch(arr, 3, 0, arr.length - 1));
    }

    /**
     * 查找算法三: 插值查找, 要求数组必须是有序的, 这里如果有多个目标值也是只返回了一个下标
     * int mid = left + (right – left) * (findVal – arr[left]) / (arr[right] – arr[left])
     *
     * @param arr
     * @param v
     * @param start
     * @param end
     * @return
     */
    public static int insertSearch(int[] arr, int v, int start, int end) {
        if (v == arr[start]) {
            return start;
        }
        if (v == arr[end]) {
            return end;
        }
        if (start >= end) {
            return -1;
        }
        int mid = start + (end - start) * (v - arr[start]) / (arr[end] - arr[start]);
        if (v > arr[start] && v < arr[end]) {
            if (v == arr[mid]) {
                return mid;
            } else if (v > arr[mid]) {
                start = mid + 1;
            } else {
                end = mid - 1;
            }
            return insertSearch(arr, v, start, end);
        } else if (v < arr[start] && v > arr[end]) {
            if (v == arr[mid]) {
                return mid;
            } else if (v > arr[mid]) {
                end = mid - 1;
            } else {
                start = mid + 1;
            }
            return insertSearch(arr, v, start, end);
        } else {
            return -1;
        }
    }

    ////////////////////////////// 4.斐波那契查找 //////////////////////////////
    @Test
    public void fn04() {
        int[] arr = {1,2,3,4,5,6,7,8};
        System.out.println(fibonacciSearch(arr, -1,  10));
    }

    /**
     * @param max
     * @return
     */
    private static int[] fibonacci(int max) {
        int[] arr = new int[max];
        int i = 0;
        arr[0] = 1;
        arr[1] = 1;
        for (i = 2; i < max; i ++) {
            arr[i] = arr[i - 1] + arr[i - 2];
        }
        return arr;
    }

    public static int fibonacciSearch(int[] arr, int v, int max) {
        int low = 0;
        int high = arr.length - 1;
        int mid = 0;
        // 斐波那契分割数值下标
        int k = 0;
        // 序列元素个数
        int i = 0;
        // 获取斐波那契数列
        int[] fibonacciArr = fibonacci(max);
        // 获取斐波那契分割值下标
        while (arr.length > fibonacciArr[k] - 1) {
            k++;
        }
        // 创建临时数组
        int[] tmp = new int[fibonacciArr[k] - 1];
        System.arraycopy(arr, 0, tmp, 0, arr.length);
        // 序列补充至 fibonacciArr[k] 个元素
        for (i = arr.length; i < fibonacciArr[k] - 1; i++) {
            tmp[i] = tmp[high];
        }
        while (low <= high) {
            // low：起始位置
            // 前半部分有f[k-1]个元素，由于下标从0开始
            // 则-1 获取 黄金分割位置元素的下标
            mid = low + fibonacciArr[k - 1] - 1;
            if (tmp[mid] > v) {
                // 查找前半部分，高位指针移动
                high = mid - 1;
                // （全部元素） = （前半部分）+（后半部分）
                // f[k] = f[k-1] + f[k-2]
                // 因为前半部分有f[k-1]个元素，所以 k = k-1
                k--;
            } else if (tmp[mid] < v) {
                // 查找后半部分，低位指针移动
                low = mid + 1;
                // （全部元素） = （前半部分）+（后半部分）
                // f[k] = f[k-1] + f[k-2]
                // 因为后半部分有f[k-2]个元素，所以 k = k-2
                k -= 2;
            } else {
                if (mid <= high) {
                    // 如果为真则找到相应的位置
                    return mid;
                } else {
                    // 出现这种情况是查找到补充的元素
                    // 而补充的元素与high位置的元素一样
                    return high;
                }
            }
        }
        return -1;
    }
}
