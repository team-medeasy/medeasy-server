package com.medeasy.domain.user_schedule.dto;

import lombok.*;

import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserScheduleDto {

    private Long userScheduleId;

    private String name;

    private LocalTime takeTime;

    // 값을 지정하지 않을 때 false
    private boolean isRecommended;
}
