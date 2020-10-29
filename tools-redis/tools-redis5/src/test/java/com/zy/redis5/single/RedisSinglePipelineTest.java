package com.zy.redis5.single;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class RedisSinglePipelineTest {
    @Autowired
    private JedisPool jedisPool;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    public void fn01() {
        Jedis jedis = jedisPool.getResource();
        if (Objects.nonNull(jedis)) {
            Pipeline pipeline = jedis.pipelined();
            try {
                // 1.开启事务
                pipeline.multi();

                // 2.编辑命令
                pipeline.set("k1", "1");
                pipeline.set("k2", "v2");
                // 这里模拟 命令错误, 语法正确, 但是执行错误, 发现所有仅有错误的无法执行, 其他命令正常提交
                pipeline.incr("k1"); // k1 由于是数字, 可以 incr, 执行成功;
                pipeline.incr("k2"); // k2 由于是字符串, 不可以 incr, 执行失败;

                // 3.提交事务
                // pipeline.exec();  这里如果用 exec(), 表示提交事务, 但这里的事务不完全遵循原子性:
                // 原子性: 如果是命令错误, 假设有一条执行了 不存在的 abc 命令, 则上述几个正确的命令也都将执行失败.
                // 非原子性: 如果是运行时错误, 如上例中对 k2 进行 incr, 则仅有该命令失败, 其他正确命令都将执行成功
                pipeline.exec();
                // pipeline.sync(); // 这里如果用 sync(), 将不会提交事务.
                // pipeline.discard();// 这里如果用 discard(), 表示取消事务, 上述语句不会执行
            } catch (Exception e) {
                log.error("failed to execute transaction.", e);
            } finally {
                // 4.关闭 pipeline
                pipeline.close();
                // 5.释放 jedis 连接
                jedis.close();
            }
        }
    }

    @Test
    public void fn02() {
        redisTemplate.executePipelined((RedisCallback<Void>) connection -> {
            connection.openPipeline();
            for (int i = 1; i < 4; i++) {
                byte[] key = ("k" + i).getBytes(StandardCharsets.UTF_8);
                byte[] value = ("v" + i).getBytes(StandardCharsets.UTF_8);
                connection.setEx(key, i * 100, value);
            }
            connection.closePipeline();
            return null;
        });
    }

    @Test
    public void fn03() {
        Jedis jedis = jedisPool.getResource();
        final String prefix = "prefix_";
        if (Objects.nonNull(jedis)) {
            try {
                jedis.mset(prefix + "name", "tom1",
                        prefix + "age", "20",
                        prefix + "gender", "male"
                        );
                jedis.msetnx(prefix + "name", "tom2",
                        prefix + "age", "22");
                List<String> mget = jedis.mget(prefix + "name",
                        prefix + "age",
                        prefix + "gender"
                );
                System.out.println("---------------------------------------");
                System.out.println(mget);
                System.out.println("---------------------------------------");
            } finally {
                jedis.close();
            }
        }
    }

}
