package com.zy.tools.undefined.undefined.concurrent.v2.threadlocal;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadLocalOom01 {

    private static final ThreadLocal<Task> threadLocal = new ThreadLocal<Task> ();

    // 在启动参数中配置: -Xmx50m
    // 在启动参数中配置: -Xmx50m
    // 在启动参数中配置: -Xmx50m
    public static void main(String[] args) {
        final ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < 20; i++) {
            int finalI = i;
            executorService.execute(() -> {
                try {
                    System.out.println("create obj: " + finalI);
                    Task task = new Task();
                    threadLocal.set(task);
                    task = null; // help GC
                } finally {
                    // 如果这里注掉, 将发生 OOM; 注释放开, 则不会 OOM
                    threadLocal.remove();
                }
            });
        }
    }

    private static class Task {
        // 创建一个 10m 的数组（单位转换是 1M -> 1024KB -> 1024*1024B）
        private final byte[] bytes = new byte[10 * 1024 * 1024];
    }
}
