package com.medeasy.domain.routine.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 동일한 루틴 그룹에서의 날짜 범위를 받아오기 위한 dto
 * */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoutineGroupDateRangeDto {

    private Long routineGroupId;

    private LocalDateTime updatedAt;

    private LocalDate startDate;

    private LocalDate endDate;
}
