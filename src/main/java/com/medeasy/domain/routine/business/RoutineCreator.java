package com.medeasy.domain.routine.business;

import com.medeasy.domain.routine.db.RoutineEntity;
import com.medeasy.domain.routine.dto.RoutineRegisterRequest;
import com.medeasy.domain.user.db.UserEntity;
import com.medeasy.domain.user_schedule.db.UserScheduleEntity;

import java.util.List;

public interface RoutineCreator {
    public List<RoutineEntity> createRoutines(RoutineCalculator routineCalculator, RoutineRegisterRequest request, UserEntity userEntity, List<UserScheduleEntity> userScheduleEntities);
}
