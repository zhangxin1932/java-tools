package com.zy.tools.undefined.callback;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 带超时回调的 示例
 */
public class Bootstrap02 {

    public static void main(String[] args) {
        Bootstrap02 bootstrap02 = new Bootstrap02();
        Worker worker = object -> {
            try {
                // 模拟耗时任务
                TimeUnit.SECONDS.sleep(1L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "callbackTimeout ---> " + object;
        };

        Wrapper wrapper = new Wrapper();
        wrapper.setWorker(worker);
        wrapper.setParam("just");
        // 添加结果回调器
        wrapper.setListener(System.out::println);

        CompletableFuture<Wrapper> future = CompletableFuture.supplyAsync(() -> bootstrap02.doWork(wrapper));
        try {
            future.get(500, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            // e.printStackTrace();
            wrapper.getListener().result("timeout exception.");
        }
    }

    private Wrapper doWork(Wrapper wrapper) {
        Worker worker = wrapper.getWorker();
        String result = worker.action(wrapper.getParam());
        wrapper.getListener().result(result);
        return wrapper;
    }
}
