package com.medeasy.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class KaKaoLoginRequest {

    @NotBlank(message = "access_token 값을 입력해주세요.")
    private String accessToken;

    @Schema(description = "FCM 토큰", example = "fTFT04fHRUSpZOEKYgVTFg:APA91bGRZN_yOTBUrRWb4BA9qAUoDU8brY8r7FF1w-OvFJ9NtAIVLPzg_LkjEXgTppsgt0w972gQs3FaljpP-OuYqF9kz6NW1X9GW0nKFleaP1ogHFp-Dxc")
    private String fcmToken;
}
