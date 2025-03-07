package com.medeasy.domain.user.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUsageDaysResponse {
    private Long userId;

    private Long usageDays;
}
