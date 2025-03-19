package com.medeasy.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
public class RedisConfig {

    @Value("${redis.jwt.host}")
    private String redisJwtHost;

    @Value("${redis.jwt.port}")
    private int redisJwtPort;

    @Value("${redis.alarm.host}")
    private String redisAlarmHost;

    @Value("${redis.alarm.port}")
    private int redisAlarmPort;

    // 첫 번째 Redis (6379)
    @Bean(name = "redisConnectionFactoryJwt")
    public RedisConnectionFactory redisConnectionFactoryJwt() {
        return new LettuceConnectionFactory(redisJwtHost, redisJwtPort);
    }

    // 두 번째 Redis (6380)
    @Bean(name = "redisConnectionFactoryAlarm")
    public RedisConnectionFactory redisConnectionFactoryAlarm() {
        return new LettuceConnectionFactory(redisAlarmHost, redisAlarmPort);
    }

    // 기본 RedisTemplate (Spring에서 자동 생성하려고 하는 기본 빈)
    @Bean
    public StringRedisTemplate redisTemplate() {
        return new StringRedisTemplate(redisConnectionFactoryJwt());
    }

    // 첫 번째 Redis에 대한 StringRedisTemplate
    @Bean(name = "redisTemplateForJwt")
    public StringRedisTemplate redisTemplateForJwt(RedisConnectionFactory redisConnectionFactoryJwt) {
        return new StringRedisTemplate(redisConnectionFactoryJwt);
    }

    // 두 번째 Redis에 대한 StringRedisTemplate
    @Bean(name = "alarmRedisTemplate")
    public StringRedisTemplate alarmRedisTemplate(RedisConnectionFactory redisConnectionFactoryAlarm) {
        return new StringRedisTemplate(redisConnectionFactoryAlarm);
    }
}