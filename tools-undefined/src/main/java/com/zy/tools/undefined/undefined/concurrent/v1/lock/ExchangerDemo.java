package com.zy.tools.undefined.undefined.concurrent.v1.lock;

import java.util.concurrent.Exchanger;

public class ExchangerDemo {

    public static void main(String[] args) {
        Exchanger<Integer> exchanger = new Exchanger<>();

        new Thread(() -> {
            try {
                int a = 3;
                a = exchanger.exchange(a);
                System.out.println("a------" + a);  // 10
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            try {
                int b = 10;
                b = exchanger.exchange(b);
                System.out.println("b-----" + b); // 3
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

}
