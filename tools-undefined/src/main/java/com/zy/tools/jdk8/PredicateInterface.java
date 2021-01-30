package com.zy.tools.jdk8;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.util.function.Predicate;

public class PredicateInterface {

    private static final Predicate<String> PREDICATE = StringUtils::isBlank;

    @Test
    public void fn01() {
        boolean b = PREDICATE.test("");
        System.out.println(b);
    }
}
