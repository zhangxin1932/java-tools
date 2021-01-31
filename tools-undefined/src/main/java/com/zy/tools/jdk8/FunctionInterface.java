package com.zy.tools.jdk8;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * 函数式接口编程示例
 */
public class FunctionInterface {

    /**
     * 1.一个入参, 一个出参
     */
    private static final Function<Integer, String> FUNCTION = (i) -> "function interface " + i;
    /**
     * 2.一个入参, 一个出参是 boolean 结果
     */
    private static final Predicate<String> PREDICATE = StringUtils::isBlank;
    /**
     * 3.没有入参, 一个出参是指定的值
     */
    private static final Supplier<String> SUPPLIER = () -> "hello";
    /**
     * 4.一个入参, 没有出参
     */
    private static final Consumer<String> CONSUMER = t -> {
        System.out.println("------");
        System.out.println(t);
        System.out.println("------");
    };
    /**
     * 5.没有入参, 没有出参
     */
    private static final Handler HANDLER = () -> {
        System.out.println("handler begin...");
        System.out.println("business logic...");
        System.out.println("handler end...");
    };

    @Test
    public void fn01() {
        String v = FUNCTION.apply(20);
        System.out.println(v);
    }

    @Test
    public void fn02() {
        boolean b = PREDICATE.test("");
        System.out.println(b);
    }

    @Test
    public void fn03() {
        System.out.println(SUPPLIER.get());
    }

    @Test
    public void fn04() {
        CONSUMER.accept("ddd");
    }

    @Test
    public void fn05() {
        HANDLER.handle();
    }

    interface Handler {
        void handle();
    }

}
