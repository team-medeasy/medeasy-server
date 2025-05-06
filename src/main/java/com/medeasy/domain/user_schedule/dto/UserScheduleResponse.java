package com.medeasy.domain.user_schedule.dto;

import lombok.*;

import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserScheduleResponse {

    private Long userScheduleId;

    private String name;

    private LocalTime takeTime;
}
