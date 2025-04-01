package com.medeasy.domain.user.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterCareResponse {

    private Long careGiverId;

    private Long careReceiverId;

    private LocalDateTime registerAt;
}
