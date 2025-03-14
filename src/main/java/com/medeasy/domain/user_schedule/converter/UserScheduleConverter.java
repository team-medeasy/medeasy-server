package com.medeasy.domain.user_schedule.converter;

import com.medeasy.common.annotation.Converter;
import com.medeasy.domain.user_schedule.db.UserScheduleEntity;
import com.medeasy.domain.user_schedule.dto.UserScheduleDto;

@Converter
public class UserScheduleConverter {

    public UserScheduleDto toDto(UserScheduleEntity userScheduleEntity) {
        return UserScheduleDto.builder()
                .id(userScheduleEntity.getId())
                .userId(userScheduleEntity.getUser().getId())
                .name(userScheduleEntity.getName())
                .takeTime(userScheduleEntity.getTakeTime())
                .build()
                ;
    }
}
