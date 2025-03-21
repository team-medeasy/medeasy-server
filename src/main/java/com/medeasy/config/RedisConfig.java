package com.medeasy.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;

@Configuration
public class RedisConfig {

    @Value("${redis.jwt.host}")
    private String redisJwtHost;

    @Value("${redis.jwt.port}")
    private int redisJwtPort;

    @Value("${redis.jwt.password}")
    private String redisJwtPassword;

    @Value("${redis.alarm.host}")
    private String redisAlarmHost;

    @Value("${redis.alarm.port}")
    private int redisAlarmPort;

    @Value("${redis.alarm.password}")
    private String redisAlarmPassword;


    // 첫 번째 Redis (6379)
    @Bean(name = "redisConnectionFactoryJwt")
    public RedisConnectionFactory redisConnectionFactoryJwt() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(redisJwtHost, redisJwtPort);
        config.setPassword(redisJwtPassword);

        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
                .commandTimeout(Duration.ofSeconds(2))        // ⏱️ 커맨드 타임아웃
                .shutdownTimeout(Duration.ofMillis(100))      // ⏱️ 종료 타임아웃
                .build();

        return new LettuceConnectionFactory(config, clientConfig);
    }

    // 두 번째 Redis (6380)
    @Bean(name = "redisConnectionFactoryAlarm")
    public RedisConnectionFactory redisConnectionFactoryAlarm() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(redisAlarmHost, redisAlarmPort);
        config.setPassword(redisAlarmPassword);

        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
                .commandTimeout(Duration.ofSeconds(2))        // ⏱️ 커맨드 타임아웃
                .shutdownTimeout(Duration.ofMillis(100))      // ⏱️ 종료 타임아웃
                .build();

        return new LettuceConnectionFactory(config, clientConfig);
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