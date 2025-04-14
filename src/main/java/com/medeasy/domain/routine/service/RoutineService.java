package com.medeasy.domain.routine.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medeasy.common.error.RoutineErrorCode;
import com.medeasy.common.exception.ApiException;
import com.medeasy.domain.routine.db.RoutineEntity;
import com.medeasy.domain.routine.db.RoutineQueryRepository;
import com.medeasy.domain.routine.db.RoutineRepository;
import com.medeasy.domain.routine.dto.RoutineGroupDto;
import com.medeasy.domain.user.db.UserEntity;
import com.medeasy.domain.user.service.UserService;
import com.medeasy.domain.user_schedule.converter.UserScheduleConverter;
import com.medeasy.domain.user_schedule.db.UserScheduleEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoutineService {

    private final RoutineRepository routineRepository;
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

            // 날짜 기준 그룹 생성
            routineMap.putIfAbsent(takeDate, RoutineGroupDto.builder()
                    .takeDate(takeDate)
                    .userScheduleDtos(new ArrayList<>())
                    .build()
            );
            RoutineGroupDto routineGroupDto = routineMap.get(takeDate);

            // 2. 스케줄 기준 그룹 찾기
            UserScheduleEntity userScheduleEntity = routineEntity.getUserSchedule();
            Optional<RoutineGroupDto.UserScheduleGroupDto> existingScheduleOpt = routineGroupDto.getUserScheduleDtos().stream()
                    .filter(dto -> dto.getUserScheduleId().equals(userScheduleEntity.getId()))
                    .findFirst();

            RoutineGroupDto.UserScheduleGroupDto scheduleDto;

            if (existingScheduleOpt.isPresent()) {
                scheduleDto = existingScheduleOpt.get();
            } else {
                scheduleDto = RoutineGroupDto.UserScheduleGroupDto.builder()
                        .userScheduleId(userScheduleEntity.getId())
                        .name(userScheduleEntity.getName())
                        .takeTime(userScheduleEntity.getTakeTime())
                        .routineDtos(new ArrayList<>())
                        .build();

                routineGroupDto.getUserScheduleDtos().add(scheduleDto);
            }


            // 3. 루틴을 RoutineDto로 변환하여 추가
            RoutineGroupDto.UserScheduleGroupDto.RoutineDto routineDto =
                    RoutineGroupDto.UserScheduleGroupDto.RoutineDto.builder()
                            .routineId(routineEntity.getId())
                            .nickname(routineEntity.getNickname())
                            .isTaken(routineEntity.getIsTaken())
                            .dose(routineEntity.getDose())
                            .medicineId(routineEntity.getMedicineId())
                            .build();

            scheduleDto.getRoutineDtos().add(routineDto);
        }

        return new ArrayList<>(routineMap.values());
    }


    public RoutineEntity getRoutineById(Long id) {
        return routineRepository.findById(id).orElseThrow(() -> new ApiException(RoutineErrorCode.NOT_FOUND_ROUTINE));
    }

    public RoutineEntity getUserRoutineById(Long userId, Long id) {
        return routineRepository.findByUserIdAndId(userId, id).orElseThrow(() -> new ApiException(RoutineErrorCode.NOT_FOUND_ROUTINE));
    }

    public void deleteRoutineByUserIdAndId(Long userId, Long routineId) {
        routineRepository.deleteByUserIdAndId(userId, routineId);
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
     * 루틴 조회 및 필요 시 배치 생성
     */
    public List<RoutineEntity> getOrCreateRoutines(
            Long userId,
            List<UserScheduleEntity> userScheduleEntities,
            List<LocalDate> takeDates
    ) {
        UserEntity userEntity = userService.getUserById(userId);
        List<Long> userScheduleIds = userScheduleEntities.stream()
                .map(UserScheduleEntity::getId)
                .toList();

        List<RoutineEntity> existingRoutines = routineRepository
                .findAllByByUserIdUserScheduleIdsAndTakeDates(userId, userScheduleIds, takeDates);
        List<RoutineEntity> newRoutines = new ArrayList<>();

        Set<String> existingKeys = existingRoutines.stream()
                .map(r -> r.getUserSchedule().getId() + "_" + r.getTakeDate())
                .collect(Collectors.toSet());

        for (UserScheduleEntity schedule : userScheduleEntities) {
            for (LocalDate takeDate : takeDates) {
                String key = schedule.getId() + "_" + takeDate;
                if (!existingKeys.contains(key)) {
                    RoutineEntity newRoutine = RoutineEntity.builder()
                            .user(userEntity)
                            .userSchedule(schedule)
                            .takeDate(takeDate)
                            .build();
                    newRoutines.add(newRoutine);
                    existingKeys.add(key);
                    existingRoutines.add(newRoutine); // 최종 결과 리스트에 추가
                }
            }
        }

        if (!newRoutines.isEmpty()) {
            routineRepository.saveAll(newRoutines);
        }

        return existingRoutines;
    }


    /**
     * 루틴 리스트를 Map<"userScheduleId_takeDate", RoutineEntity>로 변환
     */
    public Map<String, RoutineEntity> toRoutineMap(List<RoutineEntity> routines) {
        return routines.stream()
                .collect(Collectors.toMap(
                        r -> r.getUserSchedule().getId() + "_" + r.getTakeDate(),
                        r -> r
                ));
    }

    public List<String> getDistinctRoutineByUserId(Long userId) {
        return routineRepository.findDistinctMeidicneIdByUserId(userId);
    }


}
