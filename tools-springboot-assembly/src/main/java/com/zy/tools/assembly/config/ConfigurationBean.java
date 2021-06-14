package com.zy.tools.assembly.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.zy.commons.lang.security.AESTools;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;

import javax.sql.DataSource;

@Configuration
@PropertySource("config.properties")
@ImportResource(locations = {"classpath:applicationContext.xml"})
public class ConfigurationBean {

    @Value("${did.db.driverClassName}")
    private String driverClassName;

    @Value("${did.db.url}")
    private String url;

    @Value("${did.db.username}")
    private String username;

    @Value("${did.db.password}")
    private String password;

    @Value("${did.db.key}")
    private String key;

    @Bean
    public DataSource dataSourceProperties() {
        // 也可以在此处调用相关 https或 rpc 接口获取数据库的配置信息进行 DataSource 的配置
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(url);
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUsername(username);
        dataSource.setPassword(AESTools.decryptAES(password, key, AESTools.AESType.AES_ECB_128, null));
        return dataSource;
    }

}
