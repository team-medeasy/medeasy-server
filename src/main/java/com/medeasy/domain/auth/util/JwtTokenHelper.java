package com.medeasy.domain.auth.util;

import com.medeasy.common.error.TokenErrorCode;
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
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class JwtTokenHelper implements TokenHelperIfs{

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
        LocalDateTime expiredLocalDateTime=LocalDateTime.now().plusHours(accessTokenPlusHour);
        Date expiredAt= Date.from(expiredLocalDateTime.atZone(ZoneId.systemDefault()).toInstant()); //LocalDateTime->Date
        SecretKey key= Keys.hmacShaKeyFor(secretKey.getBytes()); // 비밀 키를 바탕으로 HMAC SHA 키 생성 // 바이트 배열로 변환

        String jwtToken= Jwts.builder() // 토큰 생성
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
        var expiredLocalDateTime= LocalDateTime.now().plusHours(refreshTokenPlusHour); // 토큰의 만료 시간
        var expiredAt= Date.from(expiredLocalDateTime.atZone(ZoneId.systemDefault()).toInstant()); // Date
        var key= Keys.hmacShaKeyFor(secretKey.getBytes()); // 키 만들기

        var jwtToken= Jwts.builder()
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
        }catch (Exception e){
            log.info("사용자 {} 로그인 중 redis refresh token 저장 오류 발생: {}", userId, e.getMessage());
        }

        return TokenDto.builder()
                .token(jwtToken)
                .expiredAt(expiredLocalDateTime)
                .build();
    }

    // access token이 만료되었을 때 refresh token 비교하여 accesstoken 재발급해주는 메서드
    @Override
    public TokenDto recreateAccessToken(String refreshToken) {
        var key=Keys.hmacShaKeyFor(secretKey.getBytes());
        var parser=Jwts.parser()
                .setSigningKey(key)
                .build();

        try {
            var result = parser.parseClaimsJws(refreshToken); // 토큰 문자열 파싱, 서명 검증, 클레임 추출 result는 Jws<Claims> 형식
            log.info("토큰 문자열 파싱 결과: {}", result);


            var data=new HashMap<>(result.getBody());
            String userIdFromToken=String.valueOf(data.get("userId"));

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
        var key=Keys.hmacShaKeyFor(secretKey.getBytes());

        var parser=Jwts.parser()
                .setSigningKey(key)
                .build();
        try{
            var result = parser.parseClaimsJws(token); // 토큰 문자열 파싱, 서명 검증, 클레임 추출 result는 Jws<Claims> 형식
            log.info("토큰 문자열 파싱 결과: {}", result);

            return new HashMap<String, Object>(result.getBody());

        }catch (Exception e){

            if(e instanceof SignatureException signatureException){
                // 토큰이 유효하지 않을때
                throw new SignatureException(
                        "유효하지 않는 토큰"
                );
            }
            else if(e instanceof ExpiredJwtException expiredJwtException){
                //  만료된 토큰
                throw new ExpiredJwtException(
                        expiredJwtException.getHeader(),
                        expiredJwtException.getClaims(),
                        "만료된 토큰"
                );
            }
            else{
                // 그외 에러
                throw new ApiException(TokenErrorCode.TOKEN_EXCEPTION, e);
            }
        }
    }
}
