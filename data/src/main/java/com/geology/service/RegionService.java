package com.geology.service;

import com.geology.common.jwt.AuthStorage;
import com.geology.common.jwt.JwtUser;
import com.geology.domain.VO.RegionNode;
import com.geology.domain.bean.CountyBean;
import com.geology.domain.bean.SingleFileGeologyType;
import com.geology.domain.bean.TrackGeomBean;
import com.geology.repository.db.mapper.GetGeologyInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RegionService {

    /* ========== 手动构造 RedisTemplate：127.0.0.1 6379 db0 ========== */
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private GetGeologyInfoMapper getGeologyInfoMapper;

    @PostConstruct
    public void init() {
        LettuceConnectionFactory factory = new LettuceConnectionFactory("182.92.234.109", 6379);
        factory.setDatabase(0);
        factory.afterPropertiesSet();

        redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(factory);

        // ******** 关键改动 ********
        StringRedisSerializer stringSer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringSer);
        redisTemplate.setHashKeySerializer(stringSer);
        redisTemplate.setHashValueSerializer(stringSer);   // Hash 的 value 也是 String
        redisTemplate.afterPropertiesSet();
    }

    /* ---------------- 一级：所有省 ---------------- */
    public List<RegionNode> listProvince() {
        Map<Object, Object> map = redisTemplate.opsForHash().entries("china:provinces");
        return map.entrySet().stream()
                .map(e -> new RegionNode(
                        Integer.valueOf(e.getKey().toString()),
                        e.getValue().toString(),
                        true))
                .collect(Collectors.toList());
    }

    /* ---------------- 二级：某省下的市 ---------------- */
    public List<RegionNode> listCity(Integer provinceId) {
        Map<Object, Object> map = redisTemplate.opsForHash().entries("china:city:" + provinceId);
        return map.entrySet().stream()
                .map(e -> new RegionNode(
                        Integer.valueOf(e.getKey().toString()),
                        e.getValue().toString(),
                        true))
                .collect(Collectors.toList());
    }

    /* ---------------- 三级：某市下的县 ---------------- */
    public List<RegionNode> listCounty(Integer cityId) {
        Map<Object, Object> map = redisTemplate.opsForHash().entries("china:county:" + cityId);
        return map.entrySet().stream()
                .map(e -> new RegionNode(
                        Integer.valueOf(e.getKey().toString()),
                        e.getValue().toString(),
                        false))
                .collect(Collectors.toList());
    }

    public CountyBean getCountyGeomByCode(String gb) {
        CountyBean countyBean = getGeologyInfoMapper.getCountyGeomByCode(gb);
        return countyBean;
    }

    public SingleFileGeologyType getMineGeologyFileByCountyCode(String gb) {
        SingleFileGeologyType singleFileGeologyType = getGeologyInfoMapper.getMineGeologyFileByCountyCode(gb);

        return singleFileGeologyType;
    }

    public SingleFileGeologyType getFaultGeologyFileByCountyCode(String gb) {
        SingleFileGeologyType singleFileGeologyType = getGeologyInfoMapper.getFaultGeologyFileByCountyCode(gb);

        return singleFileGeologyType;
    }
}