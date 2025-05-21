package com.medeasy.domain.auth.service;

import com.medeasy.common.error.ErrorCode;
import com.medeasy.common.exception.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
public class AuthCodeService {

    private final RedisTemplate redisTemplate;

    // Redis에 저장할 키 접두사
    private static final String AUTH_CODE_PREFIX = "auth:code:";
    private static final String USER_CODE_PREFIX = "auth:user:";

    @Autowired
    public AuthCodeService(
            @Qualifier("redisTemplateForJwt") RedisTemplate redisTemplate
    ){
        this.redisTemplate=redisTemplate;
    }

    // 인증 코드 유효 시간 (분)
    private static final int CODE_EXPIRY_MINUTES = 5;

    // 인증 코드 길이 (영숫자 조합)
    private static final int CODE_LENGTH = 6;

    /**
     * UUID 기반 인증 코드 생성 및 저장
     */
    public String generateAuthCode(String userId) {
        String authCode;

        // 유일한 코드가 생성될 때까지 반복
        do {
            // UUID 생성 후 대시 제거하고 알파벳과 숫자만 추출
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");

            // 영숫자만 필터링 (옵션)
            String alphanumeric = uuid.replaceAll("[^A-Za-z0-9]", "");

            // 지정된 길이로 자르기
            authCode = alphanumeric.substring(0, CODE_LENGTH).toUpperCase();

        } while (Boolean.TRUE.equals(redisTemplate.hasKey(AUTH_CODE_PREFIX + authCode)));

        redisTemplate.opsForValue().set(AUTH_CODE_PREFIX + authCode, userId, CODE_EXPIRY_MINUTES, TimeUnit.MINUTES);

        return authCode;
    }

    /**
     * auth code repo에서 user_id 조회
     * */
    public Long getUserIdByAuthCode(String authCode) {
        String userId = (String) redisTemplate.opsForValue().get(AUTH_CODE_PREFIX + authCode);
        redisTemplate.delete(AUTH_CODE_PREFIX + authCode);

        if(userId==null){
            throw new ApiException(ErrorCode.AUTH_ERROR, "잘못된 인증 코드");
        }
        return Long.parseLong(userId);
    }
}
