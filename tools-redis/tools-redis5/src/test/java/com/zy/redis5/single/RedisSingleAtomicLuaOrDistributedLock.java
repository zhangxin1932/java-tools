package com.zy.redis5.single;

import org.assertj.core.util.Lists;
import org.junit.Test;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.Codec;
import org.redisson.client.codec.StringCodec;
import org.redisson.config.Config;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 此处 demo 以 扣减库存为例, 给出了两种分布式解决方案
 * 方案1:
 *  先将商品及库存数全量加载到 redis 中, 然后借助 lua 脚本实现原子性的扣减库存, 注意这里的原子性是从 redis 中扣减库存
 * 方案2:
 *  借助 redisson 的分布式锁框架, 获取全局资源操作权限, 然后操作 DB 库存, 由于首先于 DB 的 qps, 所以并发效果并不会很好
 *  Redis当做分布式锁服务器时，可使用获取锁和释放锁的响应时间，每秒钟可用执行多少次 acquire / release 操作作为性能指标。
 *
 * 说明:
 *  可以自行写一个 controller, 启动一个项目, 借助 jmeter 等工具, 验证下并发情况
 */
public class RedisSingleAtomicLuaOrDistributedLock {

    private static RedissonClient client;
    private static Codec codec;
    private static final String KEY = "apple";
    private static final String LOCK_KEY = "lockKey";
    private static List<Object> keyList = Lists.newArrayList();
    private int count = 20;

    static {
        Config config = new Config();
        config.useSingleServer()
                .setDatabase(10)
                .setAddress("redis://192.168.0.156:6379");

        client = Redisson.create(config);
        // FIXME 这里定义了 StringCodec 类型的编解码器, 是因为其默认的编解码器是: MarshallingCodec
        // FIXME 而当使用 lua 脚本时, 要调用 lua 的 tonumber 函数 将库存(string类型) 转为 number 类型时,
        // FIXME 如果用默认的编解码器, 将会得到 nil 的结果, 会出错.
        // FIXME 故这里使用了 StringCodec 来解决, 也可以用 IntegerCodec 或 LongCodec.
        codec = StringCodec.INSTANCE;
        keyList.add(KEY);
    }

    /**************************** 方案1: 将数据全量加载至 redis 中, 在 redis 中扣减库存, 借助 lua 脚本控制并发 *******************************/
    @Test
    public void step01() {
        String luaScript = "return redis.call('set',KEYS[1],ARGV[1]);";
        Object result = client.getScript(codec).eval(RScript.Mode.READ_WRITE, luaScript, RScript.ReturnType.VALUE, keyList, 999);

        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>..");
        System.out.println(result);
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>..");
    }

    @Test
    public void step02() {
        String luaScript = "return redis.call('get', KEYS[1]);";
        Object result = client.getScript(codec).eval(RScript.Mode.READ_ONLY, luaScript, RScript.ReturnType.VALUE, keyList);

        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>..");
        System.out.println(result);
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>..");
    }

    @Test
    public void step03() {
        String luaScript =
                "if (redis.call('exists', KEYS[1]) == 0) then " +
                        "return 0; " +
                        "end;" +
                        "local count = redis.call('get', KEYS[1]); " +
                        "local decrementCount = ARGV[1]; " +
                        "local a = tonumber(count); " +
                        "local b = tonumber(decrementCount); " +
                        "if (a < b) then " +
                        "return 0; " +
                        "end; " +
                        "redis.call('set', KEYS[1], (a - b)); " +
                        "return 1; ";
        Object result = client.getScript(codec).eval(RScript.Mode.READ_ONLY, luaScript, RScript.ReturnType.VALUE, keyList, 3);

        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>..");
        System.out.println(result);
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>..");
    }

    /**************************** 方案2: 借助 redis 分布式锁 脚本控制并发 *******************************/
    @Test
    public void fn04() throws InterruptedException {
        ExecutorService executor = Executors.newCachedThreadPool();
        int tobeDecreasedCount = 3;

        for (int i = 0; i < 10; i++) {
            executor.submit(() -> {
                RLock lock = client.getLock(LOCK_KEY);
                boolean b = lock.tryLock();
                if (b) {
                    try {
                        int count = getCount();
                        if (count > tobeDecreasedCount) {
                            decreaseCount(count, tobeDecreasedCount);
                        }
                    } finally {
                        lock.unlock();
                    }
                }
            });
        }

        TimeUnit.SECONDS.sleep(10L);
        System.out.println("剩余库存量是: " + getCount());
    }

    private int getCount() {
        return count;
    }

    private void decreaseCount(int count, int no) {
        this.count = count - no;
    }
}
