package com.medeasy.domain.routine.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
/*
* 그룹형 쿼리 튜플 변환을 위한 dto
* */
public class RoutineGroupDto {
    private LocalTime takeTime;
    private List<RoutineDto> routines;
}
