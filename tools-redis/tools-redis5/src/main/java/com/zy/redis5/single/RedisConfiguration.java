package com.zy.redis5.single;

import com.zy.redis5.dto.DelayedOrderDTO;
import org.redisson.Redisson;
import org.redisson.api.*;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import redis.clients.jedis.JedisPool;

/**
 * https://github.com/redisson/redisson/wiki/%E7%9B%AE%E5%BD%95
 */
@Configuration
@ConditionalOnExpression("'${spring.profiles.active}'.equals('redis-single')")
@PropertySource("classpath:application-redis-single.yml")
public class RedisConfiguration {
    @Value("${spring.redis.host}")
    private String host;
    @Value("${spring.redis.port}")
    private int port;

    /////////////////////////////// 这一部分是 jedis 客户端 ///////////////////////////////
    @Bean
    public JedisPool jedisPool() {
        // 启动类上加 @EnableCaching
        return new JedisPool(host, port);
    }



    /////////////////////////////// 这一部分是 redisson 客户端 ///////////////////////////////

    /**
     * Redisson 中文 wiki
     * https://github.com/redisson/redisson/wiki/%E7%9B%AE%E5%BD%95
     */
    /**
     * 布隆过滤器是一种概率数据结构：
     * 能确认元素不存在于集合中，但只能提供元素出现在集合中的概率。
     *
     * Redisson还能通过RClusteredBloomFilter接口在Redis中支持分布式布隆过滤器。
     * RClusteredBloomFilter的内存效率更高，可以缩小所有Redis节点使用的内存。
     * RClusteredBloomFilter对象最多支持2^64 bit。
     * 请注意，RClusteredBloomFilter只支持Redisson集群模式使用。
     *
     * @param redissonClient
     * @return
     */
    @Bean
    public RBloomFilter<Object> redisBloomFilter(RedissonClient redissonClient) {
        RBloomFilter<Object> bloomFilter = redissonClient.getBloomFilter("redisson-bloom-filter");
        // falseProbability参数定义了使用给定RBloomFilter发生误报的概率。
        // expectedInsertions参数定义了每个元素的预期插入次数。RBloomFilter对象最多支持2^32 bit。
        bloomFilter.tryInit(55000000L, 0.03);
        return bloomFilter;
    }

    /**
     * Redisson 的有界阻塞队列
     * @param redissonClient
     * @return
     */
    @Bean
    public RBoundedBlockingQueue<DelayedOrderDTO> rBoundedBlockingQueue(RedissonClient redissonClient) {
        RBoundedBlockingQueue<DelayedOrderDTO> boundedBlockingQueue = redissonClient.getBoundedBlockingQueue("redisson-blocking-queue");
        boundedBlockingQueue.trySetCapacity(10 * 1024);
        return boundedBlockingQueue;
    }

    /**
     * Redisson 的延时队列
     * @param redissonClient
     * @param rBlockingQueue
     * @return
     */
    @Bean
    public RDelayedQueue<DelayedOrderDTO> rDelayedQueue(RedissonClient redissonClient, RBlockingQueue<DelayedOrderDTO> rBlockingQueue) {
        return redissonClient.getDelayedQueue(rBlockingQueue);
    }

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress(String.format("redis://%s:%d", host, port))
        .setDatabase(11);

        return Redisson.create(config);
    }

}
