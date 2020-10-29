package com.zy.redis5.single;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.*;
import redis.clients.jedis.params.GeoRadiusParam;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
@Slf4j
public class RedisSingleGeo {
    @Autowired
    private JedisPool jedisPool;

    /**
     * 增加地理位置的坐标
     * @param key
     * @param coordinate
     * @param memberName
     * @return
     */
    public Long geoAdd(String key, GeoCoordinate coordinate, String memberName) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.geoadd(key, coordinate.getLongitude(), coordinate.getLatitude(), memberName);
        } catch (Exception e) {
            log.error("failed to geo add.", e);
        } finally {
            release(jedis);
        }
        return null;
    }

    /**
     * 批量增加地理位置的坐标
     * @param key
     * @param memberCoordinateMap
     * @return
     */
    public Long geoAdd(String key, Map<String, GeoCoordinate> memberCoordinateMap) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.geoadd(key, memberCoordinateMap);
        } catch (Exception e) {
            log.error("failed to geo batch add.", e);
        } finally {
            release(jedis);
        }
        return null;
    }

    /**
     * 根据给定地理位置坐标获取指定范围内的地理位置集合 (返回匹配位置的经纬度 + 匹配位置与给定地理位置的距离 + 从近到远排序)
     * @param key
     * @param coordinate
     * @param radius
     * @return
     */
    public List<GeoRadiusResponse> geoRadius(String key, GeoCoordinate coordinate, double radius) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.georadius(key, coordinate.getLongitude(), coordinate.getLatitude(), radius, GeoUnit.KM, GeoRadiusParam.geoRadiusParam().withDist().withCoord().sortAscending());
        } catch (Exception e) {
            log.error("failed to geo radius.", e);
        } finally {
            release(jedis);
        }
        return null;
    }

    /**
     * 根据给定地理位置获取指定范围内的地理位置集合
     * @param key
     * @param member
     * @param radius
     * @return
     */
    public List<GeoRadiusResponse> geoRadiusByMember(String key, String member, double radius) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.georadiusByMember(key, member, radius, GeoUnit.KM, GeoRadiusParam.geoRadiusParam().withDist().withCoord().sortAscending());
        } catch (Exception e) {
            log.error("failed to geo radius by member.", e);
        } finally {
            release(jedis);
        }
        return null;
    }

    /**
     * 查询两个位置之间的距离
     * @param key
     * @param member1
     * @param member2
     * @param unit
     * @return
     */
    public Double geoDist(String key, String member1, String member2, GeoUnit unit) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.geodist(key, member1, member2, unit);
        } catch (Exception e) {
            log.error("failed to geoDist.", e);
        } finally {
            release(jedis);
        }
        return null;
    }

    /**
     * 可以获取某个地理位置的 geo hash 值
     * @param key
     * @param members
     * @return
     */
    public List<String> geoHash(String key, String... members) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.geohash(key, members);
        } catch (Exception e) {
            log.error("failed to geoHash.", e);
        } finally {
            release(jedis);
        }
        return null;
    }

    /**
     * 获取地理位置的坐标
     * @param key
     * @param member
     * @return
     */
    public List<GeoCoordinate> geoPos(String key, String... member) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.geopos(key, member);
        } catch (Exception e) {
            log.error("failed to geoPos.", e);
        } finally {
            release(jedis);
        }
        return null;
    }


    /**
     * 释放 jedis 连接
     * @param jedis
     */
    public void release(Jedis jedis) {
        if (Objects.nonNull(jedis)) {
            jedis.close();
        }
    }
}
