package com.medeasy.domain.routine.converter;

import com.medeasy.common.annotation.Converter;
import com.medeasy.domain.routine.db.RoutineEntity;
import com.medeasy.domain.routine.dto.RoutineRegisterRequest;
import com.medeasy.domain.user.db.UserEntity;
import com.medeasy.domain.user_schedule.db.UserScheduleEntity;

import java.time.LocalDate;

@Converter
public class RoutineConverter {

    public RoutineEntity toEntityFromRequest(LocalDate takeDate, String nickname, UserEntity userEntity, UserScheduleEntity userScheduleEntity, RoutineRegisterRequest request) {
        return RoutineEntity.builder()
                .takeDate(takeDate)
                .userSchedule(userScheduleEntity)
                .user(userEntity)
                .isTaken(false)
                .build()
                ;
    }
}
