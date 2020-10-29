package com.zy.redis5.hyperloglog;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.HyperLogLogOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisHyperLogLogTest {
    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String HYPER_LOG_LOG_PREFIX = "AppName_UserId_";

    @Test
    public void fn01() {
        HyperLogLogOperations<String, String> hyperLogLog = redisTemplate.opsForHyperLogLog();
        // 当发生事件 A 时(比如用户登录), 进行数据统计, key 为固定前缀 + 日期
        hyperLogLog.add(HYPER_LOG_LOG_PREFIX + "20200205", "100", "101", "102", "101");
        hyperLogLog.add(HYPER_LOG_LOG_PREFIX + "20200206", "021", "022", "023", "101");
        hyperLogLog.add(HYPER_LOG_LOG_PREFIX + "20200301", "231", "232", "233", "101");
        // 计算事件 A 某一日的统计数字
        Long daySize20200205 = hyperLogLog.size(HYPER_LOG_LOG_PREFIX + "20200205");
        System.out.println("daySize20200205 -----> " + daySize20200205);
        // 计算事件 A 整个 2 月份的统计数字: 先将 2 月份的数据合并为 HYPER_LOG_LOG_PREFIX + "202002"
        hyperLogLog.union(HYPER_LOG_LOG_PREFIX + "202002", HYPER_LOG_LOG_PREFIX + "20200205", HYPER_LOG_LOG_PREFIX + "20200206");
        Long monthSize202002 = hyperLogLog.size(HYPER_LOG_LOG_PREFIX + "202002");
        System.out.println("monthSize202002 -----> " + monthSize202002);
    }

}
