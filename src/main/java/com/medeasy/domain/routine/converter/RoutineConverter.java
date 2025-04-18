package com.medeasy.domain.routine.converter;

import com.medeasy.common.annotation.Converter;
import com.medeasy.domain.routine.db.RoutineEntity;
import com.medeasy.domain.routine.dto.RoutineRegisterRequest;
import com.medeasy.domain.routine.dto.RoutineUpdateRequest;
import com.medeasy.domain.routine_group.db.RoutineGroupEntity;
import com.medeasy.domain.user.db.UserEntity;
import com.medeasy.domain.user_schedule.db.UserScheduleEntity;

import java.time.LocalDate;

@Converter
public class RoutineConverter {

    public RoutineEntity toEntityFromRequest(LocalDate takeDate, UserEntity userEntity, UserScheduleEntity userScheduleEntity) {
        return RoutineEntity.builder()
                .takeDate(takeDate)
                .userSchedule(userScheduleEntity)
                .isTaken(false)
                .build()
                ;
    }

    public RoutineRegisterRequest toRoutineRegisterRequestFromContext(Long userId, LocalDate routineStartDate, Long startUserScheduleId, RoutineGroupEntity routineGroupEntity, RoutineUpdateRequest request, int remainingDoseTotal) {
        return RoutineRegisterRequest.builder()
                .medicineId(routineGroupEntity.getMedicineId())
                .nickname(routineGroupEntity.getNickname())
                .dose(routineGroupEntity.getDose())
                .totalQuantity(remainingDoseTotal)
                .userScheduleIds(request.getUserScheduleIds())
                .intervalDays(request.getIntervalDays())
                .startUserScheduleId(startUserScheduleId)
                .routineStartDate(routineStartDate)
                .build()
                ;
    }
}
