package com.zy.redis5.single;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import redis.clients.jedis.GeoCoordinate;
import redis.clients.jedis.GeoRadiusResponse;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisSingleGeoTest {
    @Autowired
    private RedisSingleGeo redisSingleGeo;

    private static final String GEO_KEY = "geo-redis";

    @Test
    public void fn01() {
        redisSingleGeo.geoAdd(GEO_KEY, new GeoCoordinate(118.7988762900, 32.0100383600), "雨花门地铁站");
        redisSingleGeo.geoAdd(GEO_KEY, new GeoCoordinate(118.7809642500, 32.0126691000), "中华门地铁站");
        redisSingleGeo.geoAdd(GEO_KEY, new GeoCoordinate(118.8374948900, 31.8750721900), "秣周东路地铁站");
    }

    @Test
    public void fn02() {
        List<GeoRadiusResponse> geoRadiusResponses = redisSingleGeo.geoRadius(GEO_KEY, new GeoCoordinate(118.7988762900, 32.0100383600), 5.0);
        List<GeoRadiusResponse> geoRadiusResponsesDistance = redisSingleGeo.geoRadius(GEO_KEY, new GeoCoordinate(118.7988762900, 32.0100383600), 500.0);
        System.out.println("-----------------------");
        System.out.println(JSON.toJSONString(geoRadiusResponses));
        System.out.println(JSON.toJSONString(geoRadiusResponsesDistance));
        System.out.println("-----------------------");
    }

    @Test
    public void fn03() {
        // List<GeoRadiusResponse> geoRadiusByMember = redisSingleGeo.geoRadiusByMember(GEO_KEY, "雨花门地铁站", 5.0);
        List<GeoRadiusResponse> geoRadiusByMember = redisSingleGeo.geoRadiusByMember(GEO_KEY, "雨花门", 5.0);
        System.out.println("------------------------");
        System.out.println(JSON.toJSONString(geoRadiusByMember));
        System.out.println("------------------------");
    }

}
