package com.medeasy.domain.routine.service;

import com.medeasy.common.error.RoutineErrorCode;
import com.medeasy.common.exception.ApiException;
import com.medeasy.domain.routine.db.RoutineEntity;
import com.medeasy.domain.routine.db.RoutineRepository;
import com.medeasy.domain.routine.dto.RoutineGroupDto;
import com.medeasy.domain.routine_group.db.RoutineGroupEntity;
import com.medeasy.domain.routine_group.service.RoutineGroupService;
import com.medeasy.domain.user.service.UserService;
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
    private final RoutineGroupService routineGroupService;


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
            RoutineGroupEntity routineGroupEntity = routineEntity.getRoutineGroup();

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
                            .nickname(routineGroupEntity.getNickname())
                            .isTaken(routineEntity.getIsTaken())
                            .dose(routineGroupEntity.getDose())
                            .medicineId(routineGroupEntity.getMedicineId())
                            .build();

            scheduleDto.getRoutineDtos().add(routineDto);
        }

        return new ArrayList<>(routineMap.values());
    }


    public RoutineEntity getRoutineById(Long id) {
        return routineRepository.findById(id).orElseThrow(() -> new ApiException(RoutineErrorCode.NOT_FOUND_ROUTINE));
    }

    public RoutineEntity getUserRoutineById(Long userId, Long id) {
        return routineRepository.findRoutineByUserIdAndId(userId, id).orElseThrow(() -> new ApiException(RoutineErrorCode.NOT_FOUND_ROUTINE));
    }

    public void deleteRoutineByUserIdAndId(Long userId, Long routineId) {
        routineRepository.deleteByUserIdAndId(userId, routineId);
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
