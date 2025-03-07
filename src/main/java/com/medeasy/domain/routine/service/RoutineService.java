package com.medeasy.domain.routine.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.medeasy.common.error.RoutineErrorCode;
import com.medeasy.common.exception.ApiException;
import com.medeasy.domain.routine.db.RoutineEntity;
import com.medeasy.domain.routine.db.RoutineRepository;
import com.medeasy.domain.routine.dto.RoutineDto;
import com.medeasy.domain.routine.dto.RoutineGroupDto;
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
        return routineRepository.findAllByTakeDateAndUserIdOrderByTakeTimeAsc(date, userId);
    }

    public List<RoutineGroupDto> getRoutineGroups(LocalDate date, Long userId) {
        return routineRepository.findRoutinesByTakeDateAndUserId(date, userId)
                .stream()
                .map(row -> {
                    LocalTime takeTime = ((Time) row[0]).toLocalTime();
                    String routinesJson = row[1].toString();
                    try {
                        List<RoutineDto> routineDtos = objectMapper.readValue(
                                routinesJson,
                                new TypeReference<List<RoutineDto>>() {}
                        );
                        return new RoutineGroupDto(takeTime, routineDtos);
                    } catch (Exception e) {
                        throw new RuntimeException("Error parsing JSON routines", e);
                    }
                })
                .collect(Collectors.toList());
    }


    public RoutineEntity getRoutineById(Long id) {
        return routineRepository.findById(id).orElseThrow(()-> new ApiException(RoutineErrorCode.NOT_FOUND_ROUTINE));
    }

    @Transactional
    public void deleteRoutine(Long routineId) {
        routineRepository.deleteById(routineId);
    }

    public List<Long> getRoutinesByUserId(Long userId) {
        return routineRepository.findDistinctMedicineIdByUserIdAndIsTakenIsFalse(userId);
    }
}
