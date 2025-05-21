package com.medeasy.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.SocketOptions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

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

    /**
     * Redis JSON 직렬화 설정
     */
    @Bean
    public RedisSerializer<Object> redisSerializer(ObjectMapper redisObjectMapper) {
        return new GenericJackson2JsonRedisSerializer(redisObjectMapper);
    }

    // 첫 번째 Redis (6379)
    @Bean(name = "redisConnectionFactoryJwt")
    public RedisConnectionFactory redisConnectionFactoryJwt() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(redisJwtHost, redisJwtPort);
        config.setPassword(redisJwtPassword);

        SocketOptions socketOptions = SocketOptions.builder()
                .connectTimeout(Duration.ofSeconds(1)) // ⏱️ 연결 시도 제한 시간
                .build();

        ClientOptions clientOptions = ClientOptions.builder()
                .autoReconnect(false) // ❌ 재시도 하지 않음
                .socketOptions(socketOptions)
                .build();

        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
                .commandTimeout(Duration.ofSeconds(2))        // ⏱️ 커맨드 타임아웃
                .shutdownTimeout(Duration.ofMillis(100))      // ⏱️ 종료 타임아웃
                .clientOptions(clientOptions)
                .build();

        return new LettuceConnectionFactory(config, clientConfig);
    }

    // 두 번째 Redis (6380)
    @Bean(name = "redisConnectionFactoryAlarm")
    public RedisConnectionFactory redisConnectionFactoryAlarm() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(redisAlarmHost, redisAlarmPort);
        config.setPassword(redisAlarmPassword);

        SocketOptions socketOptions = SocketOptions.builder()
                .connectTimeout(Duration.ofSeconds(1)) // ⏱️ 연결 시도 제한 시간
                .build();

        ClientOptions clientOptions = ClientOptions.builder()
                .autoReconnect(false) // ❌ 재시도 하지 않음
                .socketOptions(socketOptions)
                .build();

        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
                .commandTimeout(Duration.ofSeconds(2))        // ⏱️ 커맨드 타임아웃
                .shutdownTimeout(Duration.ofMillis(100))      // ⏱️ 종료 타임아웃
                .clientOptions(clientOptions)
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

    // 알림용 RedisTemplate - 제네릭 타입 문제 해결
    @Bean(name = "redisAlarmTemplate")
    public RedisTemplate<String, Object> redisAlarmTemplate(
            @Qualifier("redisConnectionFactoryAlarm") RedisConnectionFactory connectionFactory,
            RedisSerializer<Object> redisSerializer) {

        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Key Serializer
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        // Value Serializer
        template.setValueSerializer(redisSerializer);
        template.setHashValueSerializer(redisSerializer);

        template.afterPropertiesSet();
        return template;

    }
}