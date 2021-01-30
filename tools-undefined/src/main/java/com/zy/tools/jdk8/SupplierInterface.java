package com.zy.tools.jdk8;

import org.junit.Test;

import java.util.function.Supplier;

public class SupplierInterface {

    private static final Supplier<String> SUPPLIER = () -> "hello";

    @Test
    public void fn01() {
        System.out.println(SUPPLIER.get());
    }
    
}
