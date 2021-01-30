package com.zy.tools.jdk8;

import org.junit.Test;

import java.util.function.Function;

public class FunctionInterface {

    private static final Function<Integer, String> FUNCTION = (i) -> "function interface " + i;

    @Test
    public void fn01() {
        String v = FUNCTION.apply(20);
        System.out.println(v);
    }
}
