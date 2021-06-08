package com.zy.tools.undefined.undefined.concurrent.v2;

import java.util.concurrent.TimeUnit;

/**
 * 使得线程结束的几种修改方案, 不一定适用 volatile, 很多困惑
 */
public class TestVolatile {
    // 第1种修改方案, 将 flag 变为 volatile
    // private static volatile boolean flag = true;
    private static boolean flag = true;
    // 第2种修改方案, 将 count 变为 volatile
    // private static volatile int count = 0;
    // 第3种修改方案, 将 count 类型变为 Integer
    // private static Integer count = 0;
    private static int count = 0;

    public static void main(String[] args) {
        new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(2L);
                flag = false;
                System.out.println("flag 已经变为:" + flag);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        while (flag) {
            count++;
            // 第4种修改方案, 这里可以 <对 count> 执行format 或者 System.out.println 语句
            // String.format("==> %s", count);
            // System.out.println(count);
        }

        System.out.println(String.format("count is: %s", count));
    }
}
