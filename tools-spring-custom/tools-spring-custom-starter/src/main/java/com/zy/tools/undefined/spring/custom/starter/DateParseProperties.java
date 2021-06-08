package com.zy.tools.undefined.spring.custom.starter;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 编写配置文件读取类
 */
@ConfigurationProperties("com.zy.tools.spring.custom.starter")
public class DateParseProperties {
    private String format;

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }
}
