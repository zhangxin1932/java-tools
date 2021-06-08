package com.zy.tools.undefined.undefined.concurrent.v1;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class TestCallabel {

    public static void main(String[] args) {
        CallableDemo callableDemo = new CallableDemo();
        //1.执行 Callable 方式，需要 FutureTask 实现类的支持，用于接收运算结果。
        FutureTask<Integer> futureTask = new FutureTask<>(callableDemo);
        new Thread(futureTask).start();
        //2.接收线程运算后的结果
        try {
            Integer result = futureTask.get();
            System.out.println("======================" + result + "======================");
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}

/*
 * 一、创建执行线程的方式三：实现 Callable 接口。
 *      相较于实现 Runnable 接口的方式，方法可以有返回值，并且可以抛出异常。
 *
 * 二、执行 Callable 方式，需要 FutureTask 实现类的支持，用于接收运算结果。
 *      FutureTask 是  Future 接口的实现类
 *      Callable 需要依赖FutureTask ， FutureTask 也可以用作闭锁。
 */
class CallableDemo implements Callable<Integer> {

    // 此处返回值与泛型中一致,并抛出了异常
    @Override
    public Integer call() throws Exception {
        int sum = 0;
        for (int i = 0; i < 101; i++) {
            sum += i;
        }
        return sum;
    }
}