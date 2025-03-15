package com.medeasy.domain.routine.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.medeasy.common.error.RoutineErrorCode;
import com.medeasy.common.exception.ApiException;
import com.medeasy.domain.routine.db.RoutineEntity;
import com.medeasy.domain.routine.db.RoutineRepository;
import com.medeasy.domain.routine.dto.RoutineDto;
import com.medeasy.domain.routine.dto.RoutineGroupDto;
import com.medeasy.domain.user.db.UserEntity;
import com.medeasy.domain.user_schedule.db.UserScheduleEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoutineService {

    private final RoutineRepository routineRepository;
    private final ObjectMapper objectMapper;

    public RoutineEntity save(RoutineEntity routineEntity) {
        return routineRepository.save(routineEntity);
    }

    public void saveAll(List<RoutineEntity> routineEntities) {
        routineRepository.saveAll(routineEntities);
    }

    public List<RoutineEntity> getRoutineListByDate(LocalDate date, Long userId) {
//        return routineRepository.findAllByTakeDateAndUserIdOrderByTakeTimeAsc(date, userId);
        return null;
    }

    public List<RoutineGroupDto> getRoutineGroups(LocalDate date, Long userId) {
//        return routineRepository.findRoutinesByTakeDateAndUserId(date, userId)
//                .stream()
//                .map(row -> {
//                    LocalTime takeTime = ((Time) row[0]).toLocalTime();
//                    String routinesJson = row[1].toString();
//                    try {
//                        List<RoutineDto> routineDtos = objectMapper.readValue(
//                                routinesJson,
//                                new TypeReference<List<RoutineDto>>() {}
//                        );
//                        return new RoutineGroupDto(takeTime, routineDtos);
//                    } catch (Exception e) {
//                        throw new RuntimeException("Error parsing JSON routines", e);
//                    }
//                })
//                .collect(Collectors.toList());
        return null;
    }


    public RoutineEntity getRoutineById(Long id) {
        return routineRepository.findById(id).orElseThrow(() -> new ApiException(RoutineErrorCode.NOT_FOUND_ROUTINE));
    }

    @Transactional
    public void deleteRoutine(Long routineId) {
        routineRepository.deleteById(routineId);
    }

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
}
