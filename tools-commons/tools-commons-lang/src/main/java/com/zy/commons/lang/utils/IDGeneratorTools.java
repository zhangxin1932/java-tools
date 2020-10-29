package com.zy.commons.lang.utils;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;

/**
 * ID 生成工具类
 */
public final class IDGeneratorTools {
    private IDGeneratorTools() {
        throw new RuntimeException("IDGeneratorTools can not instantiated.");
    }

    private static final Snowflake snowflake = IdUtil.createSnowflake(1L, 1L);

    /*
     * snowflake 算法生成的 分布式 ID
     * @return
     */
    public synchronized static long generateSnowflakeID() {
        return snowflake.nextId();
    }

    public synchronized static String generateSnowflakeIDStr() {
        return snowflake.nextIdStr();
    }

}
