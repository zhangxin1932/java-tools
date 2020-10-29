package com.zy.commons.lang;

import com.zy.commons.lang.utils.IDGeneratorTools;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class IDGeneratorToolsTest {

    private static final ExecutorService executor = Executors.newCachedThreadPool();
    private static final int count = 15;
    private static final CountDownLatch countDownLatch = new CountDownLatch(count);
    @Test
    public void fn01() throws InterruptedException {
        for (int i = 0; i < 15; i++) {
            executor.submit(() -> {
                System.out.println(IDGeneratorTools.generateSnowflakeID());
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
        executor.shutdown();
    }
}
