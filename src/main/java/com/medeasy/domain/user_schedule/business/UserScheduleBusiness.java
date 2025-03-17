package com.medeasy.domain.user_schedule.business;

import com.medeasy.common.annotation.Business;
import com.medeasy.domain.user.db.UserEntity;
import com.medeasy.domain.user_schedule.converter.UserScheduleConverter;
import com.medeasy.domain.user_schedule.db.MedicationTime;
import com.medeasy.domain.user_schedule.db.UserScheduleEntity;
import com.medeasy.domain.user_schedule.dto.UserScheduleGroupDto;
import com.medeasy.domain.user_schedule.service.UserScheduleService;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Business
@RequiredArgsConstructor
public class UserScheduleBusiness {

    private final UserScheduleService userScheduleService;
    private final UserScheduleConverter userScheduleConverter;

    public List<UserScheduleGroupDto> registerUserDefaultSchedule(UserEntity userEntity) {

        // 기본 아침, 점심, 식사 후 스케줄 등록
        List<MedicationTime> defaultMedicationTimes = List.of(
                    MedicationTime.MORNING_AFTER_MEAL,
                    MedicationTime.LUNCH_AFTER_MEAL,
                    MedicationTime.DINNER_AFTER_MEAL
                );

        List<UserScheduleEntity> userScheduleEntities=defaultMedicationTimes.stream().map(defaultMedicationTime -> {
            return UserScheduleEntity.builder()
                    .name(defaultMedicationTime.getName())
                    .takeTime(defaultMedicationTime.getTakeTime())
                    .user(userEntity)
                    .build()
            ;
        }).toList();

        List<UserScheduleEntity> newUserScheduleEntities=userScheduleService.saveAll(userScheduleEntities);

        return newUserScheduleEntities.stream().map(userScheduleConverter::toDto).toList();
    }
}
