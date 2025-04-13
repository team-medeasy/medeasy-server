package com.medeasy.domain.routine.business;

import com.medeasy.domain.routine.dto.RoutineRegisterRequest;

import java.time.LocalDate;
import java.util.List;

public interface RoutineCalculator {
    List<LocalDate> calculateRoutineDates(LocalDate startDate, int scheduleSize, RoutineRegisterRequest routineRegisterRequest);
}
