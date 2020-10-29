package com.zy.redis5.single;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@RunWith(SpringRunner.class)
public class RedisSingleDistributedLockTest {

    @Autowired
    private static RedissonClient redissonClient;

    private static final String REDISSON_LOCK_PREFIX = "lock_";
    private static final ExecutorService executor = Executors.newCachedThreadPool();
    private static final int num = 20;
    private static final CountDownLatch countDownLatch = new CountDownLatch(num);

    /**
     * 这里模拟库存数量, 假设是 100
     */
    private int goodsAmount = 100;

    @Test
    public void fn01() throws InterruptedException {
        for (int i = 0; i < num; i++) {
            // executor.submit(this::minusGoods); // 不加锁扣减库存
            executor.submit(this::minusGoodsByRedissonLock); // 加分布式锁扣减库存
        }
        countDownLatch.await();
        System.out.println("goodsAmount ---------- " + goodsAmount);
    }

    /**
     * 加分布式锁扣减库存
     */
    private void minusGoodsByRedissonLock() {
        RLock lock = redissonClient.getLock(REDISSON_LOCK_PREFIX);
        try {
            lock.lock(30L, TimeUnit.SECONDS);
            goodsAmount -= 3;
        } finally {
            countDownLatch.countDown();
            lock.unlock();
        }
    }

    /**
     * 不加锁扣减库存
     */
    private void minusGoods() {
        try {
            goodsAmount -= 3;
        } finally {
            countDownLatch.countDown();
        }
    }
}
