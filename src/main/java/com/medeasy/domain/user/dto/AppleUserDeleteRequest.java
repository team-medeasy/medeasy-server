package com.medeasy.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppleUserDeleteRequest {
    @NotBlank
    private String refreshToken;
}
