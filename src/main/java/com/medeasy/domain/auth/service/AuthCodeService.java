package com.medeasy.domain.auth.service;

import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
public class AuthCodeService {

    // 발급된 인증 코드를 저장하는 맵 (코드 -> 사용자 ID)
    private final ConcurrentHashMap<String, AuthCodeInfo> authCodes = new ConcurrentHashMap<>();

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

        } while (authCodes.containsKey(authCode));

        // 인증 코드 정보 저장 (만료 시간 설정)
        long expiryTime = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(CODE_EXPIRY_MINUTES);
        authCodes.put(authCode, new AuthCodeInfo(userId, expiryTime));

        return authCode;
    }

    /**
     * 인증 코드 검증
     */
    public boolean verifyAuthCode(String authCode, String targetUserId) {
        // 인증 코드 존재 여부 확인
        AuthCodeInfo info = authCodes.get(authCode);
        if (info == null) {
            return false;
        }

        // 만료 시간 확인
        if (System.currentTimeMillis() > info.expiryTime) {
            // 만료된 코드 제거
            authCodes.remove(authCode);
            return false;
        }

        // 사용된 코드 제거 (일회용)
        authCodes.remove(authCode);

        // 계정 연동을 위한 로직 추가 (예: 두 계정 정보 연결)
        linkAccounts(info.userId, targetUserId);

        return true;
    }

    /**
     * auth code repo에서 user_id 조회
     * */
    public Long getUserIdByAuthCode(String authCode) {
        AuthCodeInfo info = authCodes.get(authCode);
        return Long.parseLong(info.userId);
    }

    /**
     * 계정 연동 처리 로직 (실제 구현 필요)
     */
    private void linkAccounts(String sourceUserId, String targetUserId) {
        // 계정 연동 로직 구현
        // 예: accountRepository.linkAccounts(sourceUserId, targetUserId);
    }

    /**
     * 만료된 인증 코드 정리 (스케줄링 작업으로 주기적 실행 권장)
     */
    public void cleanupExpiredCodes() {
        long currentTime = System.currentTimeMillis();
        authCodes.entrySet().removeIf(entry -> entry.getValue().expiryTime < currentTime);
    }

    /**
     * 인증 코드 정보 저장을 위한 내부 클래스
     */
    private static class AuthCodeInfo {
        private final String userId;
        private final long expiryTime;

        public AuthCodeInfo(String userId, long expiryTime) {
            this.userId = userId;
            this.expiryTime = expiryTime;
        }
    }
}
