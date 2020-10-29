package com.zy.redis5.single;

import com.google.common.collect.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import redis.clients.jedis.BitOP;
import redis.clients.jedis.BitPosParams;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.nio.charset.StandardCharsets;
import java.util.BitSet;
import java.util.List;
import java.util.Objects;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisSingleBitMapTest {
    @Autowired
    private JedisPool jedisPool;
    private static final String BITMAP_PREFIX = "App_Active_User_";
    private static final List<String> loginDates = Lists.newArrayList(
            BITMAP_PREFIX + "20200206",
            BITMAP_PREFIX + "20200207",
            BITMAP_PREFIX + "20200208",
            BITMAP_PREFIX + "20200209");

    /**
     * jedis.setbit
     */
    @Test
    public void fn01() {
        Jedis jedis = jedisPool.getResource();
        if (Objects.nonNull(jedis)) {
            try {
                // 3个参数中, 第二位为 long 型, 第三位只能是 "0" 或 "1"
                jedis.setbit(loginDates.get(0), 1, "1");
                jedis.setbit(loginDates.get(1), 2, "1");
                jedis.setbit(loginDates.get(2), 3, "0");
                jedis.setbit(loginDates.get(3), 3, "1");
            } finally {
                // 释放资源
                jedis.close();
            }
        }
    }

    /**
     * jedis.get
     * 返回的是 某一个 key 中, setbit 时第三个参数为 "1" 的个数;
     * 比如某一天的活跃用户数
     */
    @Test
    public void fn02() {
        Jedis jedis = jedisPool.getResource();
        if (Objects.nonNull(jedis)) {
            try {
                BitSet bitSet = BitSet.valueOf(jedis.get(loginDates.get(0).getBytes(StandardCharsets.UTF_8)));
                int loginAmount20200206 = bitSet.cardinality();
                System.out.println("jedis.get(key) ------- loginAmount20200206 >>>>>>>>>>>> " + loginAmount20200206);
            } finally {
                jedis.close();
            }
        }
    }

    /**
     * jedis.get
     * 返回的是 多个 key 中, setbit 时第三个参数为 "1" 的个数;
     * 比如某个月的活跃用户数
     */
    @Test
    public void fn03() {
        Jedis jedis = jedisPool.getResource();
        if (Objects.nonNull(jedis)) {
            try {
                BitSet bitSet = new BitSet();
                loginDates.forEach(loginDate -> {
                    BitSet value = BitSet.valueOf(jedis.get(loginDate.getBytes(StandardCharsets.UTF_8)));
                    bitSet.or(value);
                });
                int loginAmount202002 = bitSet.cardinality();
                System.out.println("jedis.get(key) ------- loginAmount202002 >>>>>>>>>>>> " + loginAmount202002);
            } finally {
                jedis.close();
            }
        }
    }

    /**
     * jedis.getbit
     * 返回某个 key 的某个 offset 对应的 value 是否是 1
     * 当某个 key 的 offset 对应的 value, setbit 时的 第三个参数为 "0", 此处返回 false; 第三个参数为 "1", 返回 true;
     * 比如某一天某个用户是否登录
     */
    @Test
    public void fn04() {
        Jedis jedis = jedisPool.getResource();
        if (Objects.nonNull(jedis)) {
            try {
                Boolean getbit = jedis.getbit(loginDates.get(0), 1);
                System.out.println("jedis.getbit(key) ------- loginAmount20200206 >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> " + getbit);
            } finally {
                jedis.close();
            }
        }
    }

    /**
     * jedis.bitcount(key)
     * jedis.bitcount(key, start, end) // start, end 最终转为 byte
     */
    @Test
    public void fn05() {
        Jedis jedis = jedisPool.getResource();
        if (Objects.nonNull(jedis)) {
            try {
                Long bitcount = jedis.bitcount(loginDates.get(0));
                System.out.println("jedis.bitcount(key) >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> " + bitcount);
                Long bitcountFromBegin2End = jedis.bitcount(loginDates.get(0), 0, 1);
                System.out.println("jedis.bitcount(key, begin, end) >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> " + bitcountFromBegin2End);
            } finally {
                jedis.close();
            }
        }
    }

    /**
     * jedis.bitpos(key, value)
     * jedis.bitpos(key, value, BitPosParams), BitPosParams 中的 start, end 单位是 byte
     * 返回位图中第一个值为bit的二进制位的位置
     */
    @Test
    public void fn06() {
        Jedis jedis = jedisPool.getResource();
        if (Objects.nonNull(jedis)) {
            try {
                Long bitpos = jedis.bitpos(loginDates.get(0), true);
                System.out.println("jedis.bitpos(key, value) >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> " + bitpos);
                Long bitposFromByte1ToByte2 = jedis.bitpos(loginDates.get(0), true, new BitPosParams(1, 2));
                System.out.println("jedis.bitpos(key, value, BitPosParams) >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> " + bitposFromByte1ToByte2);
            } finally {
                jedis.close();
            }
        }
    }

    /**
     * jedis.bitop
     * 对一个或多个保存二进制位的字符串 key 进行位元操作，并将结果保存到 destkey 上。
     */
    @Test
    public void fn07() {
        Jedis jedis = jedisPool.getResource();
        if (Objects.nonNull(jedis)) {
            try {
                jedis.bitop(BitOP.OR, BITMAP_PREFIX + "202002", loginDates.get(0), loginDates.get(1), loginDates.get(2), loginDates.get(3));
                Long bitopAnd = jedis.bitcount(BITMAP_PREFIX + "202002");
                System.out.println("jedis.bitop >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> " + bitopAnd);
            } finally {
                jedis.close();
            }
        }
    }

    /**
     * jedis.bitfield
     */
    @Test
    public void fn08() {
        Jedis jedis = jedisPool.getResource();
        if (Objects.nonNull(jedis)) {
            try {
                List<Long> bitfield = jedis.bitfield(BITMAP_PREFIX + "202002");
                System.out.println("jedis.bitfield >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> " + bitfield);
            } finally {
                jedis.close();
            }
        }
    }

}
