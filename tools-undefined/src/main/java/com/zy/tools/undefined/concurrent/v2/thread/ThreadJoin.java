package com.zy.tools.undefined.concurrent.v2.thread;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * t.join 阻塞的是主线程, 适于通过join方法来等待线程执行的结果的应用，其实有点类似future/callable的功能。
 */
public class ThreadJoin {
    public static void main(String[] args) {
        AtomicBoolean flag = new AtomicBoolean(false);
        Thread t = new Thread(() -> {
            flag.set(true);
            System.out.println("线程 t 执行完毕");
        });
        t.start();
        try {
            // 这里阻塞的是主线程, t 线程的内容, 永远在 t.join 后面的代码之前执行
            // 如果把 t.join 注释掉, 可以发现, 后面的代码可能先于 t 线程里的内容执行
            t.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 这里在 t.join 完成后, 执行
        System.out.println("flag value is: " + flag.get());
    }
}
