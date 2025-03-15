package com.medeasy.domain.routine.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medeasy.common.error.ErrorCode;
import com.medeasy.common.error.RoutineErrorCode;
import com.medeasy.common.exception.ApiException;
import com.medeasy.domain.routine.db.RoutineEntity;
import com.medeasy.domain.routine.db.RoutineRepository;
import com.medeasy.domain.routine.dto.RoutineGroupDto;
import com.medeasy.domain.routine_medicine.converter.RoutineMedicineConverter;
import com.medeasy.domain.routine_medicine.db.RoutineMedicineEntity;
import com.medeasy.domain.user.db.UserEntity;
import com.medeasy.domain.user_schedule.converter.UserScheduleConverter;
import com.medeasy.domain.user_schedule.db.UserScheduleEntity;
import com.medeasy.domain.user_schedule.dto.UserScheduleDto;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class RoutineService {

    private final RoutineRepository routineRepository;
    private final ObjectMapper objectMapper;
    private final RoutineMedicineConverter routineMedicineConverter;
    private final UserScheduleConverter userScheduleConverter;

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
            Optional<UserScheduleDto> existingSchedule = routineGroupDto.getUserScheduleDtos().stream()
                    .filter(s -> s.getUserScheduleId().equals(userScheduleEntity.getId()))
                    .findFirst();

            UserScheduleDto scheduleDTO;

            if (existingSchedule.isPresent()) {
                scheduleDTO = existingSchedule.get();
            } else {
                scheduleDTO = userScheduleConverter.toDto(userScheduleEntity);
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
