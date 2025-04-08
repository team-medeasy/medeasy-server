package com.medeasy.domain.routine.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoutineFlatDto {
    private Long routineGroupId;

    private LocalDate takeDate;
    private Long userScheduleId;

    private String medicineId;
    private String nickname;
    private int dose;
}
