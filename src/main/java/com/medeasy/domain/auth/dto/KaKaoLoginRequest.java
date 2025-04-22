package com.medeasy.domain.auth.dto;

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
}
