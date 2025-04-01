package com.medeasy.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterCareReceiverRequest {
    @NotBlank
    private String email;

    @NotBlank
    private String password;
}
