package com.medeasy.domain.auth.service;

import com.medeasy.common.error.ErrorCode;
import com.medeasy.common.error.TokenErrorCode;
import com.medeasy.common.exception.ApiException;
import com.medeasy.domain.auth.dto.*;
import com.medeasy.domain.auth.util.TokenHelperIfs;
import com.medeasy.domain.user.dto.UserDto;
import com.medeasy.domain.user.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppleService {

    private final TokenHelperIfs tokenHelper;
    private final UserService userService;
    private final WebClient webClient;

    @Value("${apple.client-id}")
    private String clientId;

    @Value("${apple.team-id}")
    private String teamId;

    @Value("${apple.key-id}")
    private String keyId;

    @Value("${apple.private-key-path}")
    private String privateKeyPath;

    public AppleUserProfile verifyAppleToken(String identityToken) {
        try {
            // JWT 검증 및 클레임 추출
            Jws<Claims> claimsJws = Jwts.parser()
                    .setAllowedClockSkewSeconds(60) // 1분의 클럭 스큐 허용
                    .requireIssuer("https://appleid.apple.com")
                    .requireAudience(clientId)
                    .build()
                    .parseClaimsJws(identityToken);

            // 토큰에서 클레임 추출
            Claims claims = claimsJws.getBody();

            // 만료 시간 확인
            Date expirationDate = claims.getExpiration();
            if (expirationDate != null && expirationDate.before(new Date())) {
                throw new ApiException(TokenErrorCode.INVALID_TOKEN, "애플 ID 토큰이 만료되었습니다");
            }

            String email=claims.get("email", String.class);
            String appleUserID = claims.get("sub", String.class);

            return new AppleUserProfile(appleUserID, email);

        } catch (SignatureException e) {
            log.error("애플 토큰 서명 검증 실패: {}", e.getMessage());
            throw new ApiException(TokenErrorCode.INVALID_TOKEN, "유효하지 않은 애플 ID 토큰 서명");
        } catch (ExpiredJwtException e) {
            log.error("애플 토큰 만료: {}", e.getMessage());
            throw new ApiException(TokenErrorCode.EXPIRED_TOKEN, "애플 ID 토큰이 만료되었습니다");
        } catch (Exception e) {
            log.error("애플 토큰 검증 중 오류 발생: {}", e.getMessage());
            throw new ApiException(TokenErrorCode.INVALID_TOKEN, "애플 ID 토큰 검증 실패");
        }
    }

    // 애플 클라이언트 시크릿 생성 (특정 시나리오에서 필요)
    public String generateAppleClientSecret() {
        try {
            // 개인 키 파일에서 로드
            Resource resource = new ClassPathResource(privateKeyPath.replace("classpath:", ""));
            byte[] keyBytes = FileCopyUtils.copyToByteArray(resource.getInputStream());
            String privateKey = new String(keyBytes, StandardCharsets.UTF_8);

            // 헤더와 푸터 제거 (있는 경우)
            privateKey = privateKey
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s", "");

            // 개인 키 파싱
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKey));
            KeyFactory keyFactory = KeyFactory.getInstance("EC");
            PrivateKey key = keyFactory.generatePrivate(keySpec);

            // 현재 시간 및 만료
            Date now = new Date();
            Date expiration = new Date(now.getTime() + 15777000000L); // 6개월

            // 클라이언트 시크릿 JWT 생성 (JJWT 라이브러리 사용)
            String clientSecret = Jwts.builder()
                    .issuer(teamId)
                    .issuedAt(now)
                    .expiration(expiration)
                    .audience().add("https://appleid.apple.com").and()
                    .subject(clientId)
                    .header().keyId(keyId).and()
                    .signWith(key)
                    .compact();

            return clientSecret;
        } catch (Exception e) {
            log.error("애플 클라이언트 시크릿 생성 오류: {}", e.getMessage());
            throw new ApiException(ErrorCode.SERVER_ERROR, "애플 클라이언트 시크릿 생성 실패");
        }
    }
}