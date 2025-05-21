package com.medeasy.domain.auth.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AppleAuthResponse {
    private String accessToken;
    private String refreshToken;
    private LocalDateTime accessTokenExpiry;
    private LocalDateTime refreshTokenExpiry;
    private String userId;
}