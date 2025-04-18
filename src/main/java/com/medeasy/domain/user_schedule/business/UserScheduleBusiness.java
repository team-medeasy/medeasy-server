package com.medeasy.domain.user_schedule.business;

import com.medeasy.common.annotation.Business;
import com.medeasy.common.error.SchedulerError;
import com.medeasy.common.exception.ApiException;
import com.medeasy.domain.routine.db.RoutineEntity;
import com.medeasy.domain.user.db.UserEntity;
import com.medeasy.domain.user_schedule.db.MedicationTime;
import com.medeasy.domain.user_schedule.db.UserScheduleEntity;
import com.medeasy.domain.user_schedule.service.UserScheduleService;
import lombok.RequiredArgsConstructor;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Business
@RequiredArgsConstructor
public class UserScheduleBusiness {

    private final UserScheduleService userScheduleService;

    public void registerUserDefaultSchedule(UserEntity userEntity) {

        // 기본 아침, 점심, 식사 후 스케줄 등록
        List<MedicationTime> defaultMedicationTimes = List.of(
                    MedicationTime.MORNING,
                    MedicationTime.LUNCH,
                    MedicationTime.DINNER,
                    MedicationTime.BED_TIME
                );

        List<UserScheduleEntity> userScheduleEntities=defaultMedicationTimes.stream().map(defaultMedicationTime -> {
            return UserScheduleEntity.builder()
                    .name(defaultMedicationTime.getName())
                    .takeTime(defaultMedicationTime.getTakeTime())
                    .user(userEntity)
                    .build()
            ;
        }).toList();
    }

    /**
     * 요청에 들어있는 user schedule ids가 사용자 스케줄 엔티티에 존재하는 것인지 검증하는 함수
     * 시간 별로 오름차순하여 List<UserScheduleEntity> 반환
     * */
    public List<UserScheduleEntity> validationRequest(List<UserScheduleEntity> userScheduleEntities, List<Long> requestScheduleIds) {
        // request 에 포함된 schedule 정보 가져오기
        List<UserScheduleEntity> registerUserScheduleEntities = userScheduleEntities.stream()
                .filter(userScheduleEntity -> requestScheduleIds.contains(userScheduleEntity.getId()))
                .sorted(Comparator.comparing(UserScheduleEntity::getTakeTime))
                .toList();

        // 요청에 들어간 user_schedule_id가 존재하지 않을 경우 예외 발생
        if(registerUserScheduleEntities.size() != requestScheduleIds.size()){
            throw new ApiException(SchedulerError.NOT_FOUND);
        }

        return registerUserScheduleEntities;
    }

    /**
     * 루틴 리스트를 기반으로 복용 스케줄 조회
     * */
    public List<Long> getDistinctUserScheduleIds(List<RoutineEntity> routineEntities) {
        return routineEntities.stream()
                .map(RoutineEntity::getUserSchedule)
                .filter(Objects::nonNull)
                .distinct() // 중복된 UserScheduleEntity 제거
                .sorted(Comparator.comparing(UserScheduleEntity::getTakeTime)) // takeTime 기준 오름차순
                .map(UserScheduleEntity::getId)
                .toList();
    }
}
