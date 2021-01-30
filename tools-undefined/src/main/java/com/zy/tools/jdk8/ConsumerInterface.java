package com.zy.tools.jdk8;

import org.junit.Test;

import java.util.function.Consumer;

public class ConsumerInterface {

    private static final Consumer<String> CONSUMER = t -> {
        System.out.println("------");
        System.out.println(t);
        System.out.println("------");
    };

    @Test
    public void fn01() {
        CONSUMER.accept("ddd");
    }
}
