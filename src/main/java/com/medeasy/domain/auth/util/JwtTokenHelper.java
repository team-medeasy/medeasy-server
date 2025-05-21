package com.medeasy.domain.auth.util;

import com.medeasy.common.error.TokenErrorCode;
import com.medeasy.common.error.UserErrorCode;
import com.medeasy.common.exception.ApiException;
import com.medeasy.domain.auth.dto.TokenDto;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class JwtTokenHelper implements TokenHelperIfs {

    private final StringRedisTemplate redisJwtTemplate;

    // 명시적으로 @Autowired를 사용하고 @Qualifier를 적용
    @Autowired
    public JwtTokenHelper(@Qualifier("redisTemplateForJwt") StringRedisTemplate redisJwtTemplate) {
        this.redisJwtTemplate = redisJwtTemplate;
    }

    @Value("${token.secret.key}")
    private String secretKey;
    @Value("${token.access-token.plus-hour}")
    private Long accessTokenPlusHour;
    @Value("${token.refresh-token.plus-hour}")
    private Long refreshTokenPlusHour;

    @Override
    public TokenDto issueAcessToken(Map<String, Object> data) {
        LocalDateTime expiredLocalDateTime = LocalDateTime.now().plusHours(accessTokenPlusHour);
        Date expiredAt = Date.from(expiredLocalDateTime.atZone(ZoneId.systemDefault()).toInstant()); //LocalDateTime->Date
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes()); // 비밀 키를 바탕으로 HMAC SHA 키 생성 // 바이트 배열로 변환

        String jwtToken = Jwts.builder() // 토큰 생성
                .signWith(key, SignatureAlgorithm.HS256) // 서명 생성
                .setClaims(data) // 데이터를 클레임으로 설정, 클레임은 JWT 페이로드에 포함되는 정보, 사용자 식별 정보, 권한, 기타 메타데이터 포함
                .setExpiration(expiredAt) // JWT의 만료시간을 설정
                .compact();

        return TokenDto.builder() // token -> 데이터 전송 객체
                .token(jwtToken)
                .expiredAt(expiredLocalDateTime)
                .build();
    }

    @Override
    public TokenDto issueRefreshToken(Map<String, Object> data) {
        var expiredLocalDateTime = LocalDateTime.now().plusHours(refreshTokenPlusHour); // 토큰의 만료 시간
        var expiredAt = Date.from(expiredLocalDateTime.atZone(ZoneId.systemDefault()).toInstant()); // Date
        var key = Keys.hmacShaKeyFor(secretKey.getBytes()); // 키 만들기

        var jwtToken = Jwts.builder()
                .signWith(key, SignatureAlgorithm.HS256)
                .setClaims(data)
                .setExpiration(expiredAt)
                .compact();

        // 예) data에 userId라는 key가 있다고 가정
        String userId = String.valueOf(data.get("userId"));

        // Redis에 (refreshToken -> userId) 저장 + TTL 설정(시간 단위 HOUR)
        // refreshToken이 만료되면 자동으로 레디스에서 제거되도록
        try {
            redisJwtTemplate.opsForValue().set(
                    userId,
                    jwtToken,
                    refreshTokenPlusHour,
                    TimeUnit.HOURS
            );
        } catch (Exception e) {
            log.error("사용자 {} 로그인 중 redis refresh token 저장 오류 발생: {}", userId, e.getMessage());
        }

        return TokenDto.builder()
                .token(jwtToken)
                .expiredAt(expiredLocalDateTime)
                .build();
    }

    // access token이 만료되었을 때 refresh token 비교하여 accesstoken 재발급해주는 메서드
    @Override
    public TokenDto recreateAccessToken(String refreshToken) {
        var key = Keys.hmacShaKeyFor(secretKey.getBytes());
        var parser = Jwts.parser()
                .setSigningKey(key)
                .build();

        try {
            var result = parser.parseClaimsJws(refreshToken); // 토큰 문자열 파싱, 서명 검증, 클레임 추출 result는 Jws<Claims> 형식
            log.info("토큰 문자열 파싱 결과: {}", result);


            var data = new HashMap<>(result.getBody());
            String userIdFromToken = String.valueOf(data.get("userId"));

            if (userIdFromToken == null || userIdFromToken.isEmpty()) {
                throw new ApiException(TokenErrorCode.INVALID_TOKEN, "토큰 내 user 정보가 없습니다.");
            }

            String storedRefreshToken = redisJwtTemplate.opsForValue().get(userIdFromToken);
            if (storedRefreshToken == null) {
                // Redis에 토큰이 없다면 이미 만료되었거나, 로그아웃되었을 수 있음
                throw new ApiException(TokenErrorCode.INVALID_TOKEN, "Redis에 저장된 Refresh Token이 없습니다.");
            }

            // 4) 요청으로 들어온 Refresh Token과 Redis에 저장된 토큰이 일치하는지 확인
            if (!storedRefreshToken.equals(refreshToken)) {
                throw new ApiException(TokenErrorCode.INVALID_TOKEN, "Refresh Token 불일치");
            }

            return issueAcessToken(data);

        } catch (SignatureException e) {
            // 서명 검증 실패
            throw new ApiException(TokenErrorCode.INVALID_TOKEN, "토큰 서명 검증 실패");
        } catch (ExpiredJwtException e) {
            // 토큰 만료
            throw new ApiException(TokenErrorCode.EXPIRED_TOKEN, "Refresh Token이 만료되었습니다.");
        } catch (ApiException e) {
            // 위에서 직접 던진 ApiException은 그대로 다시 던짐
            throw e;
        }
    }

    @Override
    public Map<String, Object> validationTokenWithThrow(String token) {
        var key = Keys.hmacShaKeyFor(secretKey.getBytes());

        var parser = Jwts.parser()
                .setSigningKey(key)
                .build();
        try {
            var result = parser.parseClaimsJws(token); // 토큰 문자열 파싱, 서명 검증, 클레임 추출 result는 Jws<Claims> 형식
            log.info("토큰 문자열 파싱 결과: {}", result);

            return new HashMap<String, Object>(result.getBody());

        } catch (Exception e) {

            if (e instanceof SignatureException signatureException) {
                // 토큰이 유효하지 않을때
                throw new SignatureException(
                        "유효하지 않는 토큰"
                );
            } else if (e instanceof ExpiredJwtException expiredJwtException) {
                //  만료된 토큰
                throw new ExpiredJwtException(
                        expiredJwtException.getHeader(),
                        expiredJwtException.getClaims(),
                        "만료된 토큰"
                );
            } else {
                // 그외 에러
                throw new ApiException(TokenErrorCode.TOKEN_EXCEPTION, e);
            }
        }
    }

    /**
     * 이메일, 카카오 UID 및 비밀키를 사용하여 안전한 비밀번호 생성
     *
     * @param email    사용자 이메일
     * @param kakaoUid 카카오 사용자 ID
     * @return 해싱된 비밀번호
     */
    public String generateSecurePassword(String email, String kakaoUid) {
        // 애플리케이션의 비밀키 (환경 변수나 설정 파일에서 가져오는 것이 좋음)

        String combinedString = email + ":" + kakaoUid + ":" + secretKey;

        try {
            // SHA-256 해싱 알고리즘 사용
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(combinedString.getBytes(StandardCharsets.UTF_8));

            // 바이트를 16진수 문자열로 변환
            StringBuilder hexString = new StringBuilder();
            for (byte hashByte : hashBytes) {
                String hex = Integer.toHexString(0xff & hashByte);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            // 더 안전한 대안으로 BCrypt 사용 (SHA-256을 사용할 수 없는 경우)
            return BCrypt.hashpw(combinedString, BCrypt.gensalt(12));
        }
    }

    public String getUserIdFromRefreshToken(String refreshToken) {
        var key = Keys.hmacShaKeyFor(secretKey.getBytes());
        var parser = Jwts.parser()
                .setSigningKey(key)
                .build();

        try {
            var result = parser.parseClaimsJws(refreshToken);
            var claims = result.getBody();

            // Extract userId from claims
            String userId = String.valueOf(claims.get("userId"));

            if (userId == null || userId.isEmpty()) {
                throw new ApiException(TokenErrorCode.INVALID_TOKEN, "토큰 내 user 정보가 없습니다.");
            }

            // Verify that this refresh token is actually stored in Redis for this userId
            String storedRefreshToken = redisJwtTemplate.opsForValue().get(userId);
            if (storedRefreshToken == null) {
                throw new ApiException(TokenErrorCode.INVALID_TOKEN, "Redis에 저장된 Refresh Token이 없습니다.");
            }

            // Check if the provided token matches the stored token
            if (!storedRefreshToken.equals(refreshToken)) {
                throw new ApiException(TokenErrorCode.INVALID_TOKEN, "Refresh Token 불일치");
            }

            return userId;

        } catch (SignatureException e) {
            throw new ApiException(TokenErrorCode.INVALID_TOKEN, "토큰 서명 검증 실패");
        } catch (ExpiredJwtException e) {
            throw new ApiException(TokenErrorCode.EXPIRED_TOKEN, "Refresh Token이 만료되었습니다.");
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(TokenErrorCode.TOKEN_EXCEPTION, e);
        }
    }

    /**
     * Retrieve the refresh token for a given userId
     *
     * @param userId the userId to lookup the refresh token for
     * @return the refresh token associated with the userId or null if not found
     */
    public String getRefreshTokenByUserId(String userId) {
        try {
            if (userId == null || userId.isEmpty()) {
                throw new ApiException(UserErrorCode.USER_NOT_FOUNT, "사용자 ID가 없습니다.");
            }

            // Get the refresh token from Redis using the userId as key
            String refreshToken = redisJwtTemplate.opsForValue().get(userId);

            // Log the lookup result (for debugging purposes)
            if (refreshToken == null) {
                log.info("사용자 ID {}에 대한 Refresh Token이 Redis에 없습니다.", userId);
            } else {
                log.debug("사용자 ID {}에 대한 Refresh Token을 조회했습니다.", userId);
            }

            return refreshToken;
        } catch (Exception e) {
            log.error("사용자 ID {}에 대한 Refresh Token 조회 중 오류 발생: {}", userId, e.getMessage());
            throw new ApiException(TokenErrorCode.TOKEN_EXCEPTION, "Refresh Token 조회 실패");
        }
    }
}
