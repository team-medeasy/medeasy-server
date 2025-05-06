package com.medeasy.domain.user_schedule.converter;

import com.medeasy.common.annotation.Converter;
import com.medeasy.common.error.SchedulerError;
import com.medeasy.common.exception.ApiException;
import com.medeasy.domain.user_schedule.db.MedicationTime;
import com.medeasy.domain.user_schedule.db.UserScheduleEntity;
import com.medeasy.domain.user_schedule.dto.UserScheduleDto;
import com.medeasy.domain.user_schedule.dto.UserScheduleResponse;

import java.util.Comparator;
import java.util.List;
import java.util.Map;


@Converter
public class UserScheduleConverter {

    public UserScheduleDto toDto(UserScheduleEntity userScheduleEntity) {
        return UserScheduleDto.builder()
                .userScheduleId(userScheduleEntity.getId())
                .name(userScheduleEntity.getName())
                .takeTime(userScheduleEntity.getTakeTime())
                .build()
                ;
    }

    public UserScheduleResponse toResponse(UserScheduleEntity userScheduleEntity) {
        return UserScheduleResponse.builder()
                .userScheduleId(userScheduleEntity.getId())
                .name(userScheduleEntity.getName())
                .takeTime(userScheduleEntity.getTakeTime())
                .build()
                ;
    }

    public UserScheduleDto toDtoWithRecommended(UserScheduleEntity userScheduleEntity, Boolean isRecommended) {
        return UserScheduleDto.builder()
                .userScheduleId(userScheduleEntity.getId())
                .name(userScheduleEntity.getName())
                .takeTime(userScheduleEntity.getTakeTime())
                .isRecommended(isRecommended)
                .build()
                ;
    }

    public List<UserScheduleDto> toDtoListFromMedicationTimes(Map<String, UserScheduleDto> scheduleMap, List<MedicationTime> medicationTimes) {
        medicationTimes.forEach(medicationTime -> {
            UserScheduleDto scheduleDto=scheduleMap.get(medicationTime.getName());
            if(scheduleDto==null) {
                throw new ApiException(SchedulerError.NOT_FOUND, "처방전 분석 중 스케줄을 찾을 수 없습니다.");
            }

            scheduleDto.setRecommended(true);
        });

        return scheduleMap.values().stream()
                .sorted(Comparator.comparing(UserScheduleDto::getTakeTime))
                .toList();
    }
}
