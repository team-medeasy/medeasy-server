package com.medeasy.domain.user_schedule.converter;

import com.medeasy.common.annotation.Converter;
import com.medeasy.domain.user_schedule.db.UserScheduleEntity;
import com.medeasy.domain.user_schedule.dto.UserScheduleDto;
import com.medeasy.domain.user_schedule.dto.UserScheduleGroupDto;

import java.util.ArrayList;

@Converter
public class UserScheduleConverter {

    public UserScheduleGroupDto toGroupDto(UserScheduleEntity userScheduleEntity) {
        return UserScheduleGroupDto.builder()
                .userScheduleId(userScheduleEntity.getId())
                .name(userScheduleEntity.getName())
                .takeTime(userScheduleEntity.getTakeTime())
                .routineMedicineDtos(new ArrayList<>())
                .build()
                ;
    }

    public UserScheduleDto toDto(UserScheduleEntity userScheduleEntity) {
        return UserScheduleDto.builder()
                .userScheduleId(userScheduleEntity.getId())
                .name(userScheduleEntity.getName())
                .takeTime(userScheduleEntity.getTakeTime())
                .build()
                ;
    }
}
