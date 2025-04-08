package com.medeasy.domain.routine_group.service;

import com.medeasy.domain.routine.db.RoutineQueryRepository;
import com.medeasy.domain.routine.dto.RoutineGroupDateRangeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PastRoutineStrategy implements RoutineDateRangeStrategy{

    private final RoutineQueryRepository routineQueryRepository;

    @Override
    public List<RoutineGroupDateRangeDto> findRoutineGroupDateRanges(Long userId, LocalDate startDate, LocalDate endDate) {
        return routineQueryRepository.findPastRoutineStartAndEndDateRangeByGroup(userId, startDate, endDate);
    }
}
