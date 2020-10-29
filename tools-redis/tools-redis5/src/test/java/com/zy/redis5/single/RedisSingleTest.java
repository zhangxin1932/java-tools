package com.zy.redis5.single;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisSingleTest {

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private RBloomFilter<Object> redisBloomFilter;

    /**
     * redis 的 BloomFilter
     */
    @Test
    public void fn01() {
        redisBloomFilter.add("tom1");
        redisBloomFilter.add("tom2");
        redisBloomFilter.add("tom3");
        System.out.println("-----------------------------");
        System.out.println(redisBloomFilter.contains("tom"));
        System.out.println(redisBloomFilter.contains("tom1"));
        System.out.println(redisBloomFilter.contains("tom2"));
        System.out.println(redisBloomFilter.contains("tom3"));
        System.out.println("-----------------------------");
    }
}
