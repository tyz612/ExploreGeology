package com.geology.user.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.Map;
import java.util.Set;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        // 使用StringRedisSerializer来序列化和反序列化redis的key值
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringRedisSerializer);
        template.setValueSerializer(stringRedisSerializer);

        return template;
    }


    @Bean(name = "redisTemplateMap")
    public RedisTemplate<String, Map<String, String>> redisTemplateMap(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Map<String, String>> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // 设置序列化方式，根据具体需要进行调整
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(Map.class));
        template.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(Map.class));

        template.afterPropertiesSet();
        return template;
    }

    @Bean(name = "redisTemplateSet")
    public RedisTemplate<String, Set<String>> redisTemplateSet(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Set<String>> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // 设置序列化方式
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(Set.class));
        template.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(Set.class));

        template.afterPropertiesSet();
        return template;
    }
}
