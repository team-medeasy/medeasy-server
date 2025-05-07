package com.medeasy.domain.auth.dto;

import lombok.Data;

@Data
public class KakaoUserProfile {
    private String id; // 회원번호
    private KakaoAccount kakao_account;

    @Data
    public static class KakaoAccount {
        private String email;
        private Profile profile;

        @Data
        public static class Profile {
            private String nickname;
        }
    }
}

