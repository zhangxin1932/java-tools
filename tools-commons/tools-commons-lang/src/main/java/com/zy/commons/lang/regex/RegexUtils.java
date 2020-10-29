package com.zy.commons.lang.regex;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * 正则表达式工具类
 */
public abstract class RegexUtils {
    public static final String REGEX_IPV4 = "\\b((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\b";

    /**
     *
     * @param str 待校验的字符串
     * @param regex 正则规则: 正则规则可以放入配置文件中定义, 如 classpath 下的 config.properties 文件
     *              [a-zA-Z-9_,.-]  ---->>>>这里匹配数字,字母,下划线,逗号,点,中划线
     * @return 若校验成功, 返回true,否则返回false
     */
    public static boolean validRegex(String str, String regex) {
        if (Objects.isNull(str) || Objects.isNull(regex)) {
            return false;
        }
        return Pattern.compile(regex).matcher(str).matches();
    }

}
