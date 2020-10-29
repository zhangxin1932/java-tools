package com.zy.tools.bigdecimal;

import org.junit.Test;

import java.math.BigDecimal;

public class BigDecimalDemo01 {

    @Test
    public void fn01() {
        // 赋值时, 构造器中用 String 类型, 以免丢失精度
        BigDecimal b1 = new BigDecimal("10.01");
        BigDecimal b2 = new BigDecimal("999.99");
        BigDecimal b3 = new BigDecimal("0.01");
        BigDecimal b4 = new BigDecimal("100.00");
        BigDecimal b5 = new BigDecimal("100.00");
        System.out.println(b1.add(b2));
        System.out.println(b1.add(b2).subtract(b3));
        System.out.println(b1.add(b2).subtract(b3).multiply(b4));
        System.out.println(b1.add(b2).subtract(b3).multiply(b4).divide(b5, 2, BigDecimal.ROUND_HALF_UP));
    }
}
