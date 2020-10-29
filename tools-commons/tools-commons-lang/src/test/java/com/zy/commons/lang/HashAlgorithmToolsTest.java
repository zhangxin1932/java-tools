package com.zy.commons.lang;

import com.zy.commons.lang.security.HashAlgorithmTools;
import org.junit.Test;

public class HashAlgorithmToolsTest {
    @Test
    public void fn01() {
        String encrypt = HashAlgorithmTools.encryptByHmacSHA256("hello", "lll");
        System.out.println(encrypt);
    }

    @Test
    public void fn02() {
        String encrypt = HashAlgorithmTools.encryptByHmacSHA1("hello", "lll");
        System.out.println(encrypt);
    }

    @Test
    public void fn03() {
        String encrypt = HashAlgorithmTools.encryptByMD5("hello");
        System.out.println(encrypt);
    }

    @Test
    public void fn04() {
        String encrypt = HashAlgorithmTools.encryptBySHA1("hello");
        System.out.println(encrypt);
    }

    @Test
    public void fn05() {
        String encrypt = HashAlgorithmTools.encryptBySHA256("hello");
        System.out.println(encrypt);
    }
}
