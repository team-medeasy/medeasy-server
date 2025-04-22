package com.medeasy.domain.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class KaKaoLoginRequest {
    private String accessToken;
}
