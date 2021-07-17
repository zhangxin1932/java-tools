package com.zy.tools.undefined.undefined.concurrent.v2.volatiles;

/**
 * 并发情况下: 指令重排
 */
public class VolatileReOrder01 {

    private static int i = 0;
    private static int j = 0;
    private static int a = 0;
    private static int b = 0;

    public static void main(String[] args) throws Exception {
        while (true) {
            i = 0;
            j = 0;
            a = 0;
            b = 0;
            Thread t1 = new Thread(() -> {
                a = 1;
                i = b;
            }, "t1");

            Thread t2 = new Thread(() -> {
                b = 1;
                j = a;
            }, "t2");
            t1.start();
            t2.start();
            t1.join();
            t2.join();

            if (i == 0 && j == 0) {
                // 如果没有指令重排, 可能出现的情况是:
                // i = 0, j = 1;
                // i = 1, j = 0;
                // i = 1, j = 1;
                System.out.println("i=" + i + "; j=" + j);

                // 如果出现了指令重排, 则可能出现: i = 0, j = 0
                // 可能线程t1中, 指令重排为: i = b; a = 1;
                // 可能线程t2中, 指令重排为: j = a; b = 1;
                break;
            }
        }
    }

}
