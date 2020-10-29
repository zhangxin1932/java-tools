package com.zy.tools.spring.custom.starter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 编写AutoConfigure类
 *
 * @ConditionalOnClass，当classpath下发现该类的情况下进行自动配置。
 * @ConditionalOnMissingBean，当Spring Context中不存在该Bean时。
 * @ConditionalOnProperty(prefix = "com.starter",value = "enabled",havingValue = "true")，当配置文件中com.starter.enabled=true时。
 */
@Configuration
@EnableConfigurationProperties(DateParseProperties.class)
@ConditionalOnClass(DateParse.class)
public class DateParseAutoConfigure {

    @Autowired
    private DateParseProperties properties;

    @Bean
    // @ConditionalOnProperty(prefix = "com.starter", value = "enabled", havingValue = "true")
    public DateParse dateParse() {
        return new DateParse(properties.getFormat());
    }
}
