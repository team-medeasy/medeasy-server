package com.medeasy.domain.routine.service;

import com.medeasy.domain.routine.db.RoutineEntity;
import com.medeasy.domain.routine.db.RoutineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoutineService {

    private final RoutineRepository routineRepository;

    public RoutineEntity save(RoutineEntity routineEntity) {
        return routineRepository.save(routineEntity);
    }

    public void saveAll(List<RoutineEntity> routineEntities) {
        routineRepository.saveAll(routineEntities);
    }
}
