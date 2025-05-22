package com.medeasy.domain.auth.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.medeasy.common.error.ErrorCode;
import com.medeasy.common.error.TokenErrorCode;
import com.medeasy.common.exception.ApiException;
import com.medeasy.domain.auth.dto.*;
import com.medeasy.domain.auth.util.TokenHelperIfs;
import com.medeasy.domain.user.dto.UserDto;
import com.medeasy.domain.user.service.UserService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
    private final ObjectMapper objectMapper;

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
                    .keyLocator(this::getApplePublicKey)
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

    // 애플 클라이언트 시크릿 생성 (오류 수정 버전)
    public String generateAppleClientSecret() {
        try {
            // 개인 키 파일에서 로드
            Resource resource;
            if (privateKeyPath.startsWith("classpath:")) {
                resource = new ClassPathResource(privateKeyPath.replace("classpath:", ""));
            } else if (privateKeyPath.startsWith("/") || privateKeyPath.contains(":\\")) {
                // 절대 경로인 경우 FileSystemResource 사용
                resource = new org.springframework.core.io.FileSystemResource(privateKeyPath);
            } else {
                // 상대 경로인 경우 ClassPathResource 사용
                resource = new ClassPathResource(privateKeyPath);
            }

            if (!resource.exists()) {
                log.error("Private key 파일을 찾을 수 없습니다: {}", privateKeyPath);
                throw new ApiException(ErrorCode.SERVER_ERROR, "Private key 파일을 찾을 수 없습니다");
            }

            byte[] keyBytes = FileCopyUtils.copyToByteArray(resource.getInputStream());
            String privateKeyContent = new String(keyBytes, StandardCharsets.UTF_8);
            log.debug("Private key 파일 읽기 완료, 크기: {} bytes", keyBytes.length);

            // 헤더와 푸터 제거
            String cleanedPrivateKey = privateKeyContent
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replace("-----BEGIN EC PRIVATE KEY-----", "")
                    .replace("-----END EC PRIVATE KEY-----", "")
                    .replaceAll("\\s", "");

            log.debug("Private key 정리 완료, Base64 길이: {}", cleanedPrivateKey.length());

            // Base64 디코딩 검증
            byte[] keyDataBytes;
            try {
                keyDataBytes = Base64.getDecoder().decode(cleanedPrivateKey);
                log.debug("Base64 디코딩 성공, 바이트 크기: {}", keyDataBytes.length);
            } catch (IllegalArgumentException e) {
                log.error("Private key Base64 디코딩 실패: {}", e.getMessage());
                throw new ApiException(ErrorCode.SERVER_ERROR, "Private key 형식이 올바르지 않습니다");
            }

            // PKCS#8 형식으로 키 스펙 생성
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyDataBytes);

            // KeyFactory 생성 (EC 알고리즘)
            KeyFactory keyFactory = KeyFactory.getInstance("EC");
            PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

            log.debug("Private key 파싱 성공: {}", privateKey.getAlgorithm());

            Date expirationDate = Date.from(LocalDateTime.now().plusDays(30).atZone(ZoneId.systemDefault()).toInstant());

            log.debug("JWT 생성 시작 - iss: {}, sub: {}, aud: https://appleid.apple.com", teamId, clientId);

            // 클라이언트 시크릿 JWT 생성
            String clientSecret = Jwts.builder()
                    .header()
                    .add("kid", keyId)
                    .add("alg", "ES256")
                    .and()
                    .issuer(teamId)
                    .issuedAt(new Date(System.currentTimeMillis()))
                    .expiration(expirationDate)
                    .audience().add("https://appleid.apple.com").and()
                    .subject(clientId)
                    .signWith(privateKey, SignatureAlgorithm.ES256)
                    .compact();

            log.debug("애플 클라이언트 시크릿 생성 성공");
            return clientSecret;

        } catch (Exception e) {
            log.error("애플 클라이언트 시크릿 생성 상세 오류: ", e);
            throw new ApiException(ErrorCode.SERVER_ERROR, "애플 클라이언트 시크릿 생성 실패: " + e.getMessage());
        }
    }

    private Key getApplePublicKey(Header header) {
        try {
            String kid = (String) header.get("kid");  // getKeyId() 대신 get("kid") 사용

            // Apple의 공개키 엔드포인트에서 키 가져오기
            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.getForObject(
                    "https://appleid.apple.com/auth/keys", String.class);

            JsonNode keys = objectMapper.readTree(response).get("keys");

            for (JsonNode key : keys) {
                if (kid.equals(key.get("kid").asText())) {
                    return buildPublicKey(key);
                }
            }

            throw new RuntimeException("해당 kid의 공개키를 찾을 수 없습니다: " + kid);

        } catch (Exception e) {
            throw new RuntimeException("Apple 공개키 가져오기 실패", e);
        }
    }

    private PublicKey buildPublicKey(JsonNode keyData) throws Exception {
        byte[] nBytes = Base64.getUrlDecoder().decode(keyData.get("n").asText());
        byte[] eBytes = Base64.getUrlDecoder().decode(keyData.get("e").asText());

        BigInteger modulus = new BigInteger(1, nBytes);
        BigInteger exponent = new BigInteger(1, eBytes);

        RSAPublicKeySpec keySpec = new RSAPublicKeySpec(modulus, exponent);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        return keyFactory.generatePublic(keySpec);
    }

    /**
     * Authorization code를 사용하여 Apple로부터 refresh token을 획득
     */
    public String getRefreshToken(String authorizationCode) {
        try {
            String tokenEndpoint = "https://appleid.apple.com/auth/token";

            // 요청 파라미터 설정
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("client_id", clientId);
            params.add("client_secret", generateAppleClientSecret());
            params.add("code", authorizationCode);
            params.add("grant_type", "authorization_code");

            // HTTP 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

            // Apple token endpoint 호출
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.postForEntity(tokenEndpoint, request, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                // JSON 응답 파싱
                JsonNode jsonResponse = objectMapper.readTree(response.getBody());
                String refreshToken = jsonResponse.get("refresh_token").asText();

                log.info("Apple refresh token 획득 성공");
                return refreshToken;
            } else {
                log.error("Apple refresh token 획득 실패: {}", response.getStatusCode());
                throw new ApiException(ErrorCode.SERVER_ERROR, "Apple refresh token 획득 실패");
            }

        } catch (Exception e) {
            log.error("Apple refresh token 획득 중 오류 발생: {}", e.getMessage());
            throw new ApiException(ErrorCode.SERVER_ERROR, "Apple refresh token 획득 실패: " + e.getMessage());
        }
    }

    /**
     * Apple 계정 연동 해제 (회원 탈퇴)
     */
    public void revokeAppleToken(String refreshToken) {
        try {
            String revokeEndpoint = "https://appleid.apple.com/auth/revoke";

            // 요청 파라미터 설정
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("client_id", clientId);
            params.add("client_secret", generateAppleClientSecret());
            params.add("token", refreshToken);
            params.add("token_type_hint", "refresh_token");

            // HTTP 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

            // Apple revoke endpoint 호출
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.postForEntity(revokeEndpoint, request, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                log.info("Apple 토큰 revoke 성공");
            } else {
                log.error("Apple 토큰 revoke 실패: {}", response.getStatusCode());
                throw new ApiException(ErrorCode.SERVER_ERROR, "Apple 토큰 revoke 실패");
            }

        } catch (Exception e) {
            log.error("Apple 토큰 revoke 중 오류 발생: {}", e.getMessage());
            throw new ApiException(ErrorCode.SERVER_ERROR, "Apple 토큰 revoke 실패: " + e.getMessage());
        }
    }
}