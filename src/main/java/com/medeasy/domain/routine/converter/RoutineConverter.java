package com.medeasy.domain.routine.converter;

import com.medeasy.common.annotation.Converter;
import com.medeasy.domain.routine.db.RoutineEntity;
import com.medeasy.domain.routine.dto.RoutineRegisterRequest;
import com.medeasy.domain.user.db.UserEntity;
import com.medeasy.domain.user_schedule.db.UserScheduleEntity;

import java.time.LocalDate;

@Converter
public class RoutineConverter {

    public RoutineEntity toEntityFromRequest(LocalDate takeDate, UserEntity userEntity, UserScheduleEntity userScheduleEntity, RoutineRegisterRequest request) {
        return RoutineEntity.builder()
                .nickname(request.getNickname())
                .takeDate(takeDate)
                .medicineId(request.getMedicineId())
                .dose(request.getDose())
                .userSchedule(userScheduleEntity)
                .user(userEntity)
                .isTaken(false)
                .build()
                ;
    }
}
