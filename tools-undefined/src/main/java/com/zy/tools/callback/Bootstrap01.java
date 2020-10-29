package com.zy.tools.callback;

import java.util.concurrent.TimeUnit;

/**
 * 异步回调示例
 */
public class Bootstrap01 {

    public static void main(String[] args) {
        Bootstrap01 bootstrap01 = new Bootstrap01();
        Worker worker = object -> {
            try {
                // 模拟耗时任务
                TimeUnit.SECONDS.sleep(1L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "callback ---> " + object;
        };
        Wrapper wrapper = new Wrapper();
        wrapper.setWorker(worker);
        wrapper.setParam("test");
        // 这里执行耗时任务
        bootstrap01.doWork(wrapper).setListener(result -> System.out.println(Thread.currentThread().getName() + "---" + result));
        // 这里的代码早于上一步的代码, 主线程被释放, 可以用于处理其他任务
        System.out.println(Thread.currentThread().getName());
    }

    private Wrapper doWork(Wrapper wrapper) {
        new Thread(() -> {
            Worker worker = wrapper.getWorker();
            String result = worker.action(wrapper.getParam());
            wrapper.getListener().result(result);
        }).start();
        return wrapper;
    }

}
