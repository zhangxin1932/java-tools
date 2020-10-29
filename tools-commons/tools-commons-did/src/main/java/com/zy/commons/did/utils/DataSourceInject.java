package com.zy.commons.did.utils;

import com.alibaba.druid.pool.DruidDataSource;
import com.netflix.config.DynamicProperty;
import com.zy.commons.did.enums.DidPattern;
import com.zy.commons.did.exception.DidException;
import com.zy.commons.lang.security.AESTools;
import lombok.Getter;

import java.util.Objects;

public class DataSourceInject {
    @Getter
    private DruidDataSource dataSource;

    public DataSourceInject() {
        init();
    }

    private void init() {
        try {
            // 获取配置文件中关于数据库连接的参数的信息
            // 也可以在此处调用相关 https 或 rpc 接口获取数据库的配置信息进行 DataSource 的配置
            String pattern = DynamicProperty.getInstance("did.pattern").getString();
            if (Objects.equals(pattern, DidPattern.snowflake.getCode()) || Objects.equals(pattern, DidPattern.numberSegment.getCode())) {
                this.dataSource = new DruidDataSource();
                // 数据库连接必须信息的设置
                dataSource.setDriverClassName(DynamicProperty.getInstance("did.db.driverClassName").getString());
                dataSource.setUrl(DynamicProperty.getInstance("did.db.url").getString());
                dataSource.setUsername(DynamicProperty.getInstance("did.db.username").getString());
                dataSource.setPassword(AESTools.decryptAES(DynamicProperty.getInstance("did.db.password").getString(), DynamicProperty.getInstance("did.db.key").getString(), AESTools.AESType.AES_ECB_128, null));

                // 数据库连接的基本信息的设置
                dataSource.setInitialSize(DynamicProperty.getInstance("did.db.initialSize").getInteger(2)); // 设置初始化连接数量
                dataSource.setMaxActive(DynamicProperty.getInstance("did.db.maxActive").getInteger(2)); // 设置最大连接数量
                dataSource.setMinIdle(DynamicProperty.getInstance("did.db.minIdle").getInteger(0)); // 最小空闲数
            }
        } catch (Exception e) {
            throw new DidException("cannot get init did.properties.");
        }
    }
}
