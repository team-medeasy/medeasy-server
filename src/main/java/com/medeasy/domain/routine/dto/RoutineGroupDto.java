package com.medeasy.domain.routine.dto;

import com.medeasy.domain.user_schedule.dto.UserScheduleGroupDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
/*
* 그룹형 쿼리 튜플 변환을 위한 dto
* */
public class RoutineGroupDto {
    private LocalDate takeDate;

    private List<UserScheduleGroupDto> userScheduleDtos;
}
