package com.medeasy.domain.auth.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CareAuthCodeResponse {
    private String authCode;
}
