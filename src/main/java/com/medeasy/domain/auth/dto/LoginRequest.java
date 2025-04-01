package com.medeasy.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;
import lombok.*;



@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {

    @NotBlank
    @Schema(description = "사용자 이메일", example = "test@example.com")
    private String email;

    @NotBlank
    @Schema(description = "비밀번호", example = "abcd1234")
    private String password;

    @Schema(description = "FCM 토큰", example = "fTFT04fHRUSpZOEKYgVTFg:APA91bGRZN_yOTBUrRWb4BA9qAUoDU8brY8r7FF1w-OvFJ9NtAIVLPzg_LkjEXgTppsgt0w972gQs3FaljpP-OuYqF9kz6NW1X9GW0nKFleaP1ogHFp-Dxc")
    private String fcmToken;
}
