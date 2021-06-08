package com.zy.tools.undefined.datastructure.bitmap;

import org.junit.Test;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Random;

/**
 * BitSet常见的应用场景是
 * 对海量数据的处理，可以用于对大数量的查找，去重，排序等工作，相比使用其他的方法，占用更少的空间，显著提高效率；
 * 也可以使用BitSet进行一些统计工作，比如日志分析、用户数统计等；
 * 还可以使用其方法做一些集合方面的运算，比如求并集、交集和补集等。
 *
 * 参考资源:
 * https://blog.csdn.net/kongmin_123/article/details/82257209
 */
public class BitSetDemo01 {

    /**
     * 从一堆数量大概在千万级的电话号码列表中找出所有重复的电话号码
     */
    @Test
    public void fn01() {
        // 创建一个具有10000000位的bitset　初始所有位的值为false
        BitSet bitSet = new BitSet(Integer.MAX_VALUE);
        // 将指定位的值设为true
        bitSet.set(83848559, true);
        bitSet.set(83848559, true);
        // 输出指定位的值
        System.out.println(bitSet.get(83848559));
        System.out.println(bitSet.get(83848550));
    }

    /**
     * 使用BitSet统计随机数的个数
     *
     * 有1千万个随机数，随机数的范围在1到1亿之间。现在要求写出一种算法，将1到1亿之间没有在随机数中的数求出来？
     *
     * 这里以100代替1亿，使用BitSet存放随机数并进行统计，减少内存的使用
     */
    @Test
    public void fn02() {
        Random random = new Random();
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 100; i ++) {
            list.add(random.nextInt(100));
        }
        System.out.println(String.format("产生的100个随机数数量是: %s, \n, 分别是: %s", list.size(), list));

        System.out.println(">>>>>>>>>>>>>>. ");
        BitSet bitSet = new BitSet(100);
        list.forEach(bitSet::set);
        System.out.println(String.format("产生的100个不重复的随机数的数量是: %s", bitSet.cardinality()));
        List<Integer> other = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            if (!bitSet.get(i)) {
                other.add(i);
            }
        }
        System.out.println(String.format("0-99中不在产生的随机数中的数量是: %s, \n, 分别是: %s", other.size(), other));
    }

    @Test
    public void fn03() {
        int n = 2000000;
        BitSet sieve = new BitSet(n + 1);
        for (int i = 3; i <= n; i ++) {
            sieve.set(i);
        }
        for (int i = 3; i <= n; i ++) {
            for (int j = 2; j < i; j ++) {
                if (i % j == 0) {
                    sieve.clear(i);
                    break;
                }
            }
        }
        int count = 0;
        for (int i = 3; i <= n; i ++) {
            if (sieve.get(i)) {
                count ++;
            }
        }
        count++;
        System.out.println(count);
    }
}
