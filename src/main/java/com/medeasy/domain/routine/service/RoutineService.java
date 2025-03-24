package com.medeasy.domain.routine.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medeasy.common.error.RoutineErrorCode;
import com.medeasy.common.exception.ApiException;
import com.medeasy.domain.routine.db.RoutineEntity;
import com.medeasy.domain.routine.db.RoutineRepository;
import com.medeasy.domain.routine.dto.RoutineGroupDto;
import com.medeasy.domain.routine_medicine.converter.RoutineMedicineConverter;
import com.medeasy.domain.routine_medicine.db.RoutineMedicineEntity;
import com.medeasy.domain.user.db.UserEntity;
import com.medeasy.domain.user.service.UserService;
import com.medeasy.domain.user_schedule.converter.UserScheduleConverter;
import com.medeasy.domain.user_schedule.db.UserScheduleEntity;
import com.medeasy.domain.user_schedule.dto.UserScheduleGroupDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoutineService {

    private final RoutineRepository routineRepository;
    private final ObjectMapper objectMapper;
    private final RoutineMedicineConverter routineMedicineConverter;
    private final UserScheduleConverter userScheduleConverter;
    private final UserService userService;

    public RoutineEntity save(RoutineEntity routineEntity) {
        return routineRepository.save(routineEntity);
    }

    public void saveAll(List<RoutineEntity> routineEntities) {
        routineRepository.saveAll(routineEntities);
    }

    public List<RoutineGroupDto> getRoutinesByDatesAndUserID(Long userId, LocalDate startDate, LocalDate endDate) {
        List<RoutineEntity> routineEntities=routineRepository.findGroupedRoutinesByDate(userId, startDate, endDate);
        Map<LocalDate, RoutineGroupDto> routineMap = new LinkedHashMap<>();

        for(RoutineEntity routineEntity: routineEntities) {
            LocalDate takeDate=routineEntity.getTakeDate();
            UserScheduleEntity userScheduleEntity = routineEntity.getUserSchedule();
            List<RoutineMedicineEntity> routineMedicineEntities = routineEntity.getRoutineMedicines();

            routineMap.putIfAbsent(takeDate, new RoutineGroupDto(takeDate, new ArrayList<>()));
            RoutineGroupDto routineGroupDto = routineMap.get(takeDate);

            // UserSchedule 찾기
            Optional<UserScheduleGroupDto> existingSchedule = routineGroupDto.getUserScheduleDtos().stream()
                    .filter(s -> s.getUserScheduleId().equals(userScheduleEntity.getId()))
                    .findFirst();

            UserScheduleGroupDto scheduleDTO;

            if (existingSchedule.isPresent()) {
                scheduleDTO = existingSchedule.get();
            } else {
                scheduleDTO = userScheduleConverter.toGroupDto(userScheduleEntity);
                routineGroupDto.getUserScheduleDtos().add(scheduleDTO);
            }

            routineMedicineEntities.forEach(entity->{
                scheduleDTO.getRoutineMedicineDtos().add(routineMedicineConverter.toDto(entity));
            });
        }

        return new ArrayList<>(routineMap.values());
    }


    public RoutineEntity getRoutineById(Long id) {
        return routineRepository.findById(id).orElseThrow(() -> new ApiException(RoutineErrorCode.NOT_FOUND_ROUTINE));
    }

    // TODO 약 개수 반환 수정
    public List<Long> getRoutinesByUserId(Long userId) {
//        return routineRepository.findDistinctMedicineIdByUserIdAndIsTakenIsFalse(userId);
        return null;
    }

    /**
     * 사용자의 날짜, 시간대가 같은 루틴을 조회
     * 있다면 routine을 반환하여 하나의 routine의 여러개의 routine medicine 등록
     * 없다면 새로 등록.
     * */
    public RoutineEntity getRoutineByUserScheduleAndTakeDate(UserEntity userEntity, UserScheduleEntity userScheduleEntity, LocalDate takeDate) {
        return routineRepository.findByUserScheduleIdAndTakeDate(userScheduleEntity.getId(), takeDate)
                .orElseGet(() -> {
                    RoutineEntity newRoutineEntity = RoutineEntity.builder()
                            .user(userEntity)
                            .userSchedule(userScheduleEntity)
                            .takeDate(takeDate)
                            .build();
                    return routineRepository.save(newRoutineEntity);
                });
    }

    /**
     * 루틴 조회 배치 처리
     * 필요한 루틴들을 가져오고, 루틴이 존재하지 않다면 배치로 생성
     * */
    public Map<String ,RoutineEntity> getRoutinesWithUserSchedulesAndTakeDates(Long userId, List<UserScheduleEntity> userScheduleEntities, List<LocalDate> takeDates) {
        UserEntity userEntity = userService.getUserById(userId);
        List<Long> userScheduleIds = userScheduleEntities.stream().map(UserScheduleEntity::getId).toList();
        List<RoutineEntity> existingRoutines=routineRepository.findAllByByUserIdUserScheduleIdsAndTakeDates(userId, userScheduleIds, takeDates);

        Map<String, RoutineEntity> routineMap = existingRoutines.stream()
                .collect(Collectors.toMap(
                        r -> r.getUserSchedule().getId() + "_" + r.getTakeDate(),
                        r -> r
                ));

        List<RoutineEntity> newRoutines = new ArrayList<>();

        for (UserScheduleEntity schedule : userScheduleEntities) {
            for (LocalDate takeDate : takeDates) {
                String key = schedule.getId() + "_" + takeDate;

                // 루틴이 없으면 생성하여 리스트에 추가
                routineMap.computeIfAbsent(key, k -> {
                    RoutineEntity newRoutine = RoutineEntity.builder()
                            .user(userEntity)
                            .userSchedule(schedule)
                            .takeDate(takeDate)
                            .build();
                    newRoutines.add(newRoutine);
                    return newRoutine;
                });
            }
        }
        // 새로 생성된 루틴 저장 (배치 처리)
        if (!newRoutines.isEmpty()) {
            routineRepository.saveAll(newRoutines);
        }

        return routineMap;
    }
}
