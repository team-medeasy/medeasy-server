package com.medeasy.domain.routine_group.service;

import com.medeasy.domain.routine.dto.RoutineGroupDateRangeDto;

import java.time.LocalDate;
import java.util.List;

public interface RoutineDateRangeStrategy {
    List<RoutineGroupDateRangeDto> findRoutineGroupDateRanges(Long userId, LocalDate startDate, LocalDate endDate);
}
