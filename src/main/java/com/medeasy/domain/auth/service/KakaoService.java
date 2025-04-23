package com.medeasy.domain.auth.service;

import com.medeasy.domain.auth.dto.KakaoUserProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class KakaoService {

    private final WebClient webClient;

    @Value("${kakao.api.user-info-uri}")
    private String userInfoUri;

    public KakaoUserProfile getUserInfo(String kakaoAccessToken) {
        return webClient.get()
                .uri(userInfoUri)
                .headers(headers -> {
                    headers.setBearerAuth(kakaoAccessToken);
                    headers.set("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
                })
                .retrieve()
                .bodyToMono(KakaoUserProfile.class)
                .block();
    }
}
