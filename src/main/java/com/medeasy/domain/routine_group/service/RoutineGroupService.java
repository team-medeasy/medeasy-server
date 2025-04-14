package com.medeasy.domain.routine_group.service;

import com.medeasy.domain.routine.db.RoutineEntity;
import com.medeasy.domain.routine.db.RoutineRepository;
import com.medeasy.domain.routine_group.db.RoutineGroupEntity;
import com.medeasy.domain.routine_group.db.RoutineGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoutineGroupService {

    private final RoutineGroupRepository routineGroupRepository;
    private final RoutineRepository routineRepository;

    /**
     * 루틴 등록시 RoutineGroup과 Routine의 연관관계 매핑
     * */
    public void mappingRoutineGroup(List<RoutineEntity> routineEntities) {

        RoutineGroupEntity routineGroupEntity= new RoutineGroupEntity();
        routineGroupRepository.save(routineGroupEntity);

        routineEntities.forEach(r -> r.setRoutineGroup(routineGroupEntity));
    }
}
