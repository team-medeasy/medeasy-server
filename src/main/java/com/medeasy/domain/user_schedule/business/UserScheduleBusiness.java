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

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

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

        userScheduleService.saveAll(userScheduleEntities);
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
     *
     * 업데이트 시간 이후로 조회
     * */
    public List<Long> getDistinctUserScheduleIds(List<RoutineEntity> routineEntities, LocalDate updateDate, LocalTime updateTime) {
        // routine list를 updateDate와 같을 경우 그 이후까지에 대해서 리스트 자르기
        // takeDate가 updateDate보다 크거나 같은 첫 index를 찾기


        int startIndex = 0;
        for (int i = 0; i < routineEntities.size(); i++) {
            LocalDate takeDate = routineEntities.get(i).getTakeDate();
            if (takeDate != null && !takeDate.isBefore(updateDate)) { // 루틴의 복용날짜가 루틴 그룹 업데이트 날짜와 같거나 큰 경우
                startIndex = i;
                break;
            }
        }
        List<RoutineEntity> filterTakeDateRoutines=routineEntities.subList(startIndex, routineEntities.size());

        // routine의 user_schedule.take_time이 updateDate보다 이후인 경우 그 이후에대해서 리스트자르기
        startIndex = 0;
        for (int i = 0; i < filterTakeDateRoutines.size(); i++) {
            LocalTime takeTime = filterTakeDateRoutines.get(i).getUserSchedule().getTakeTime();
            if (takeTime != null && !takeTime.isBefore(updateTime)) { // 루틴의 복용 시간이 루틴 그룹의 업데이트 시간과 같거나 큰 경우
                startIndex = i;
                break;
            }
        }
        List<RoutineEntity> filterTakeDateAndTakeTimeRoutines= filterTakeDateRoutines.subList(startIndex, filterTakeDateRoutines.size());

        return filterTakeDateAndTakeTimeRoutines.stream()
                .map(RoutineEntity::getUserSchedule)
                .filter(Objects::nonNull)
                .distinct() // 중복된 UserScheduleEntity 제거
                .sorted(Comparator.comparing(UserScheduleEntity::getTakeTime)) // takeTime 기준 오름차순
                .map(UserScheduleEntity::getId)
                .toList();
    }

    public List<Long> sortUserScheduleIdsByTakeTimeAsc(List<Long> userScheduleIds) {
        return userScheduleIds.stream().map(userScheduleService::findById)
                .toList()
                .stream().sorted(Comparator.comparing(UserScheduleEntity::getTakeTime))
                .map(UserScheduleEntity::getId)
                .toList();
    }
}
