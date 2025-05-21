package com.medeasy.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

// 요청 DTO
@Data
@Builder
public class AppleLoginRequest {
    private String identityToken;
    private String firstName; // 애플 최초 로그인 시 제공
    private String lastName;
    @Schema(description = "FCM 토큰", example = "fTFT04fHRUSpZOEKYgVTFg:APA91bGRZN_yOTBUrRWb4BA9qAUoDU8brY8r7FF1w-OvFJ9NtAIVLPzg_LkjEXgTppsgt0w972gQs3FaljpP-OuYqF9kz6NW1X9GW0nKFleaP1ogHFp-Dxc")
    private String fcmToken;
}