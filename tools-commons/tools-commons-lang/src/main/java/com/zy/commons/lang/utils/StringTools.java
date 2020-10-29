package com.zy.commons.lang.utils;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * 加密算法工具类
 * 字符串, 字节流 转换
 * 进制转换
 * Base64 转换
 */
public final class StringTools {

    private StringTools() {
        throw new RuntimeException("StringTools can not instantiated.");
    }

    /**
     * 将二进制转换成16进制
     *
     * @param buf UTF-8 编码的字节流
     * @return
     */
    public static String parseByte2HexStr(byte[] buf) {
        if (Objects.isNull(buf)) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        String hex;
        for (int i = 0; i < buf.length; i++) {
            hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * 将16进制转换为二进制
     *
     * @param hexStr
     * @return
     */
    public static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1) {
            return null;
        }
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2),16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }

    /**
     * @param bytes UTF-8 编码的字节
     * @return 字符串
     */
    public static String newString(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        return new String(bytes, StandardCharsets.UTF_8);
    }

    /**
     * @param string 字符串
     * @return UTF-8 编码的字节
     */
    public static byte[] getBytes(String string) {
        if (string == null) {
            return null;
        }
        return string.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * 按字符数截短
     * 发生截短时将在字符串尾添加...（在size < 3时不添加）
     * 返回的字符串的字符数<=size
     *
     * @param string 原字符串
     * @param size   字符数
     * @return 截短后的字符串
     */
    public static String shortenByChars(String string, int size) {
        if (size < 0) {
            throw new IllegalArgumentException("size can not be negative");
        }
        if (string == null || string.length() <= size) {
            return string;
        }
        if (size < 3) {
            return string.substring(0, size);
        }
        return string.substring(0, size - 3) + "---";
    }

    /**
     * 按字节截短
     * 发生截短时将在字符串尾添加...（在capacity<3时不添加）
     * 返回的字符串在UTF-8编码下占用<=capacity个字节
     *
     * @param string   原字符串
     * @param capacity 字节容量
     * @return 截短后的字符串
     */
    public static String shortenByBytes(String string, int capacity) {
        if (capacity < 0) {
            throw new IllegalArgumentException("capacity can not be negative");
        }
        if (string == null) {
            return null;
        }
        byte[] bs = getBytes(string);
        if (bs.length <= capacity) {
            return string;
        }
        if (capacity < 3) {
            return newString(transfer2Utf8Bytes(bs, capacity));
        }
        return newString(transfer2Utf8Bytes(bs, capacity - 3)) + "---";
    }

    private static byte[] transfer2Utf8Bytes(byte[] bs, int resize) {
        if (bs.length <= resize) {
            return bs;
        }
        for (int i = resize; i >= 0; i--) {
            if (((bs[i] | 0x3F) & 0xFF) != 0xBF) {
                byte[] nbs = new byte[i];
                System.arraycopy(bs, 0, nbs, 0, i);
                return nbs;
            }
        }
        return new byte[0];
    }

}
