package com.medeasy.domain.user_schedule.dto;

import lombok.*;

import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserScheduleDto {

    private Long id;

    private Long userId;

    private String name;

    private LocalTime takeTime;
}
