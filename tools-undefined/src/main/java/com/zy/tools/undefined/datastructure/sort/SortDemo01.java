package com.zy.tools.undefined.datastructure.sort;

import org.junit.Test;
import java.util.Arrays;

/**
 * 各种排序算法
 * https://blog.csdn.net/m0_37962600/article/details/81475585
 * https://www.cnblogs.com/lizr-ithouse/p/5839384.html
 * 各种排序算法的动画比较动态演示-Json在线解析格式化工具
 * http://www.jsons.cn/sort/
 */
public class SortDemo01 {

    /////////////////////////////////////////// 1.冒泡排序 ////////////////////////////////////////////
    @Test
    public void fn01() {
        // int[] arr = {1, 3, 6, 22, 28, 31, 48};
        int[] arr = {-11, -3, 6, 22, 8, 1, 8};
        bubbleSort(arr);
        System.out.println(Arrays.toString(arr));
    }

    /**
     * 排序方式一: 冒泡排序 --> 属于交换排序
     * 第一轮交换后, 最大值到最右侧
     * 第二轮交换后, 次大值到次右侧
     * ...
     * 最后一轮交换后, 倒数第二大值到正数第二侧
     *
     * @param arr
     */
    private void bubbleSort(int[] arr) {
        int tmp;
        for (int i = 0; i < arr.length; i++) {
            int count = 0;
            for (int j = 0; j < arr.length - 1 - i; j++) {
                if (arr[j] > arr[j + 1]) {
                    tmp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = tmp;
                    count++;
                }
            }
            if (count == 0) {
                break;
            }
        }
    }

    /////////////////////////////////////////// 2.选择排序 ////////////////////////////////////////////
    @Test
    public void fn02() {
        int[] arr = {-11, -3, 6, 22, 8, 1, 8};
        selectSort(arr);
        System.out.println(Arrays.toString(arr));
    }

    /**
     * 排序方式二: 选择排序
     * 第一轮排序后, 最小值和首位的值进行交换
     * 第二轮排序后, 次小值和第二位的值进行交换
     * ...
     * 最后一轮排序后, 次大值和倒数第二位的值进行交换
     *
     * @param arr
     */
    private void selectSort(int[] arr) {
        int tmp;
        for (int i = 0; i < arr.length - 1; i++) {
            int index = i;
            for (int j = i + 1; j < arr.length; j++) {
                if (arr[index] > arr[j]) {
                    index = j;
                }
            }
            if (index != i) {
                tmp = arr[i];
                arr[i] = arr[index];
                arr[index] = tmp;
            }
        }
    }

    /////////////////////////////////////////// 3.插入排序 ////////////////////////////////////////////
    @Test
    public void fn03() {
        int[] arr = {-11, -3, 6, 22, 8, 1, 8};
        insertSort(arr);
        System.out.println(Arrays.toString(arr));
    }

    /**
     * 排序方式三: 插入排序
     * 第一轮排序后, 第二位和首位的数据比较, 看是否需要交换位置
     * 第二轮排序后, 第三位和前两位的数据比较, 看是否需要交换以及在哪里交换位置
     * ...
     * 最后一轮排序后, 最后一位和前面所有的数据比较, 看是否需要交换以及在哪里交换位置
     *
     * @param arr
     */
    private void insertSort(int[] arr) {
        // 遍历所有数据
        for (int i = 1; i < arr.length; i++) {
            // 如果当前数字, 比前一个数字小
            if (arr[i] < arr[i - 1]) {
                // 把当前遍历的数字存起来
                int temp = arr[i];
                int j;
                // 遍历当前数字前的所有数字
                for (j = i - 1; j >= 0 && temp < arr[j]; j--) {
                    // 把前一个数字赋值给后一个数字
                    arr[j + 1] = arr[j];
                }
                // 把临时变量(外层for循环的当前元素)赋值给不满足条件的后一个值
                arr[j + 1] = temp;
            }
        }
    }

    /////////////////////////////////////////// 4.希尔排序 ////////////////////////////////////////////
    @Test
    public void fn04() {
        int[] arr = {-11, -3, 6, 22, 8, 1, 8};
        // shellInsertSortByExchange(arr);
        shellInsertSortByMove(arr);
        System.out.println(Arrays.toString(arr));
    }

    /**
     * 排序方式四(一): 希尔排序(由交换法实现)
     *
     * @param arr
     */
    private void shellInsertSortByExchange(int[] arr) {
        int tmp;
        // 遍历所有的步长
        for (int step = arr.length / 2; step > 0; step /= 2) {
            // 遍历所有元素
            for (int i = step; i < arr.length; i++) {
                // 遍历本组中所有的元素: 本组中的元素互相比较, 如果前者 > 后者, 则进行位置交换
                for (int j = i - step; j >= 0; j -= step) {
                    if (arr[j] > arr[j + step]) {
                        tmp = arr[j];
                        arr[j] = arr[j + step];
                        arr[j + step] = tmp;
                    }
                }
            }
        }
    }

    /**
     * 排序方式四(二): 希尔排序(由移动法实现)
     *
     * @param arr
     */
    private void shellInsertSortByMove(int[] arr) {
        // 遍历所有的步长
        for (int step = arr.length / 2; step > 0; step /= 2) {
            // 遍历所有元素
            for (int i = step; i < arr.length; i++) {
                int j = i;
                int tmp = arr[j];
                if (arr[j] < arr[j - step]) {
                    while (j - step >= 0 && tmp < arr[j - step]) {
                        arr[j] = arr[j - step];
                        j -= step;
                    }
                    arr[j] = tmp;
                }
            }
        }
    }

    /////////////////////////////////////////// 5.快速排序 ////////////////////////////////////////////
    @Test
    public void fn05() {
        int[] arr = {-11, -3, 6, 22, 8, 1, 8};
        fastSort(arr, 0, arr.length - 1);
        System.out.println(Arrays.toString(arr));
    }

    /**
     * 排序算法5: 快速排序 (利用了递归)
     *
     * @param arr
     * @param start
     * @param end
     */
    private void fastSort(int[] arr, int start, int end) {
        if (start < end) {
            // 把数组中的第start数字定位标准数
            int standard = arr[start];
            // 记录需要排序的下标
            int low = start;
            int high = end;
            // 循环找比标准数大的数, 比标准数小的数
            while (low < high) {
                // 右边的数比标准数大
                while (low < high && standard <= arr[high]) {
                    high--;
                }
                // 用右边的数,替换左边的数
                arr[low] = arr[high];

                // 左边的数比标准数小
                while (low < high && arr[low] <= standard) {
                    low++;
                }
                // 用左边的数,替换右边的数
                arr[high] = arr[low];
            }
            // 把标准数赋给左边或右边的数都行, 此时二者已重合
            arr[low] = standard;
            // 处理所有比标准数小的数字
            fastSort(arr, start, low);
            // 处理所有比标准数大的数字
            fastSort(arr, low + 1, end);
        }
    }

    /////////////////////////////////////////// 6.归并排序 ////////////////////////////////////////////
    @Test
    public void fn06() {
        int[] arr = {-11, -3, 6, 22, 8, 1, 8};
        mergeSort(arr, 0, arr.length - 1);
        System.out.println(Arrays.toString(arr));
    }

    /**
     * 排序算法6: 归并排序 (分治策略, 利用了递归)
     * https://www.jianshu.com/p/33cffa1ce613
     *
     * @param arr
     * @param left
     * @param right
     */
    private void mergeSort(int[] arr, int left, int right) {
        if (left == right) {
            return;
        }
        int mid = left + ((right - left) >> 1);
        mergeSort(arr, left, mid);
        mergeSort(arr, mid + 1, right);
        merge(arr, left, mid, right);
    }

    private void merge(int[] arr, int left, int mid, int right) {
        int[] tmp = new int[right - left + 1];
        int i = 0;
        int p1 = left;
        int p2 = mid + 1;
        // 比较左右两部分的元素, 将小的放入 tmp 中
        while (p1 <= mid && p2 <= right) {
            tmp[i++] = arr[p1] < arr[p2] ? arr[p1++] : arr[p2++];
        }
        // 退出循环后, 依次将剩余元素加入 tmp 中
        while (p1 <= mid) {
            tmp[i++] = arr[p1++];
        }
        while (p2 <= right) {
            tmp[i++] = arr[p2++];
        }
        // 把最终的排序结果复制给原数组
        if (tmp.length >= 0) {
            System.arraycopy(tmp, 0, arr, left, tmp.length);
        }
    }

    /////////////////////////////////////////// 7.基数排序 ////////////////////////////////////////////
    @Test
    public void fn07() {
        // int[] arr = {-11, -3, 6, 22, 8, 1, 8};
        // radixSortWithinNegative(arr);
        int[] arr = {53, 3, 542, 748, 14, 214};
        radixSort(arr);
        System.out.println(Arrays.toString(arr));
    }

    /**
     * 排序算法7-1: 基数排序(这种排序不支持负数的排序)
     *  是用空间换时间的经典算法
     * @param arr
     */
    private void radixSort(int[] arr) {
        // 定义一个二维数组, 表示 10 个桶, 每一个桶就是一个一维数组
        int[][] bucket = new int[10][arr.length];
        // 为了记录每个桶中, 实际存放了多少数据, 我们定义一个一维数组来记录各个桶的每次放入的数据个数
        int[] bucketElementCount = new int[10];
        int max = getMax(arr);
        int length = (max + "").length();
        for (int i = 0; i < length; i++) {
            for (int anArr : arr) {
                // 取出每个元素中个位数的值
                int digit = ((int) (anArr / (Math.pow(10, i)))) % 10;
                // 将该值放到对应的桶中, 并将桶中的元素 自增
                bucket[digit][bucketElementCount[digit]++] = anArr;
            }
            // 遍历每一个桶, 依次取出桶中的元素, 放入原来的数组中
            int index = 0;
            for (int j = 0; j < bucketElementCount.length; j++) {
                if (bucketElementCount[j] != 0) {
                    for (int k = 0; k < bucketElementCount[j]; k ++) {
                        arr[index++] = bucket[j][k];
                    }
                }
                // 要清0
                bucketElementCount[j] = 0;
            }
        }
    }

    private int getMax(int[] arr) {
        if (arr == null || arr.length == 0) {
            throw new RuntimeException("arr cannot be empty.");
        }
        int max = arr[0];
        for (int anArr : arr) {
            if (anArr > max) {
                max = anArr;
            }
        }
        return max;
    }

    private void radixSortTrain(int[] arr) {
        // 定义一个二维数组, 表示 10 个桶, 每一个桶就是一个一维数组
        int[][] bucket = new int[10][arr.length];
        // 为了记录每个桶中, 实际存放了多少数据, 我们定义一个一维数组来记录各个桶的每次放入的数据个数
        int[] bucketElementCount = new int[10];

        /////////////////////// 第一轮 结束后: [542, 53, 3, 14, 214, 748]
        for (int anArr : arr) {
            // 取出每个元素中个位数的值
            int digit = anArr % 10;
            // 将该值放到对应的桶中, 并将桶中的元素 自增
            bucket[digit][bucketElementCount[digit]++] = anArr;
        }
        // 遍历每一个桶, 依次取出桶中的元素, 放入原来的数组中
        int index = 0;
        for (int i = 0; i < bucketElementCount.length; i++) {
            if (bucketElementCount[i] != 0) {
                for (int j = 0; j < bucketElementCount[i]; j ++) {
                    arr[index++] = bucket[i][j];
                }
            }
            // 要清0
            bucketElementCount[i] = 0;
        }

        /////////////////////// 第二轮 结束后: [3, 14, 214, 542, 748, 53]
        for (int anArr : arr) {
            // 取出每个元素中个位数的值
            int digit = anArr / 10 % 10;
            // 将该值放到对应的桶中, 并将桶中的元素 自增
            bucket[digit][bucketElementCount[digit]++] = anArr;
        }
        // 遍历每一个桶, 依次取出桶中的元素, 放入原来的数组中
        index = 0;
        for (int i = 0; i < bucketElementCount.length; i++) {
            if (bucketElementCount[i] != 0) {
                for (int j = 0; j < bucketElementCount[i]; j ++) {
                    arr[index++] = bucket[i][j];
                }
            }
            // 要清0
            bucketElementCount[i] = 0;
        }

        /////////////////////// 第三轮 结束后: [3, 14, 53, 214, 542, 748]
        for (int anArr : arr) {
            // 取出每个元素中个位数的值
            int digit = anArr / 100 % 10;
            // 将该值放到对应的桶中, 并将桶中的元素 自增
            bucket[digit][bucketElementCount[digit]++] = anArr;
        }
        // 遍历每一个桶, 依次取出桶中的元素, 放入原来的数组中
        index = 0;
        for (int i = 0; i < bucketElementCount.length; i++) {
            if (bucketElementCount[i] != 0) {
                for (int j = 0; j < bucketElementCount[i]; j ++) {
                    arr[index++] = bucket[i][j];
                }
            }
            // 要清0
            bucketElementCount[i] = 0;
        }

    }

    /**
     * 排序算法7-2: 基数排序 (这种方式支持负数的排序)
     *  是用空间换时间的经典算法
     * @param arr
     */
    private void radixSortWithinNegative(int[] arr) {
        // 基数，在循环过程中根据数的大小自动增长
        int digitNumber = 1;
        // 桶，正数和负数共20个桶
        int[][] bucket = new int[20][arr.length > 19 ? arr.length : 20];
        // i 代表当前循环的基数，如 1,10，100....
        for (int i = 1, arrOrder = 0; i <= digitNumber;arrOrder = 0) {
            // 表示本次循环中基数是否已经扩大
            boolean digitExpand = false;
            // 本次循环中 20 个桶每个桶中存的数的个数
            int[] numberAmount = new int[20];
            // 放入桶中
            for (int num : arr) {
                // digit表示 num 要放在 20 格桶中的哪一个
                int digit = (num / i) % 10;
                // 这里是加 10 ，即正数用后 10 个桶，负数用前 10 个桶
                digit += 10;
                // numberAmount[digit] 初始值为 0 ，可以直接使用
                bucket[digit][numberAmount[digit]++] = num;
                // 本次循环中遇到有以下条件时最外层循环条件需要扩大一次，即基数需要乘以10
                // 比如第一次循环时digitNumber = 1,当前 num = 2，则不需扩大
                // 若 num = 10 则需要扩大一次最外层循环
                if (num >= (digitNumber * 10) && !digitExpand) {
                    digitNumber *= 10;
                    digitExpand = true;
                }
            }
            // 从 20 个桶中取出数据，完成一次排序
            for (int j = 0; j < 20; j++) {
                for (int k = 0; k < numberAmount[j]; k++) {
                    arr[arrOrder++] = bucket[j][k];
                }
            }
            // 每循环一次 i 需要乘以 10
            i *= 10;
        }
    }
}
