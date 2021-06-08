package com.zy.tools.undefined.bit;

import org.junit.Test;

public class BitOperation {

    private byte b1 = 101;
    private byte b2 = 102;

    private short s1 = 101;
    private short s2 = 102;

    private int i1 = 101;
    private int i2 = 102;

    private long l1 = 101;
    private long l2 = 102;

    private char c1 = 101;
    private char c2 = 102;

    /**
     * 按位与 &
     */
    @Test
    public void fn01() {
        System.out.println(b1 & b2);
        System.out.println(s1 & s2);
        System.out.println(i1 & i2);
        System.out.println(l1 & l2);
        System.out.println(c1 & c2);
    }

    /**
     * 按位或 |
     */
    @Test
    public void fn02() {
        System.out.println(b1 | b2);
        System.out.println(s1 | s2);
        System.out.println(i1 | i2);
        System.out.println(l1 | l2);
        System.out.println(c1 | c2);
    }

    /**
     * 按位非 ~
     */
    @Test
    public void fn03() {
        System.out.println(~b1 + "......" + ~b2);
        System.out.println(~s1 + "......" + ~s2);
        System.out.println(~i1 + "......" + ~i2);
        System.out.println(~l1 + "......" + ~l2);
        System.out.println(~c1 + "......" + ~c2);
    }

    /**
     * 按位异或 ^
     */
    @Test
    public void fn04() {
        System.out.println(b1 ^ b2);
        System.out.println(s1 ^ s2);
        System.out.println(i1 ^ i2);
        System.out.println(l1 ^ l2);
        System.out.println(c1 ^ c2);
    }

    /**
     * 左移 <<
     */
    @Test
    public void fn05() {
        System.out.println(b1 << b2);
        System.out.println(s1 << s2);
        System.out.println(i1 << i2);
        System.out.println(l1 << l2);
        System.out.println(c1 << c2);
    }

    /**
     * 右移 >>
     */
    @Test
    public void fn06() {
        System.out.println(b1 >> b2);
        System.out.println(s1 >> s2);
        System.out.println(i1 >> i2);
        System.out.println(l1 >> l2);
        System.out.println(c1 >> c2);
    }

    /**
     * 无符号右移 >>>
     */
    @Test
    public void fn07() {
        System.out.println(b1 >>> b2);
        System.out.println(s1 >>> s2);
        System.out.println(i1 >>> i2);
        System.out.println(l1 >>> l2);
        System.out.println(c1 >>> c2);
    }

    @Test
    public void fn08() {
        System.out.println(~(-1L << 12));
        System.out.println(~(-1L << 20));
        System.out.println(~(-1L << 0));
    }

}
