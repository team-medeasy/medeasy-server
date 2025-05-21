package com.medeasy.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class AppleUserProfile {
    private String appleUserId; // 회원번호
    private String email;
}

