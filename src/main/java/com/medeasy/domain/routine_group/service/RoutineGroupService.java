package com.medeasy.domain.routine_group.service;

import com.medeasy.domain.routine.db.RoutineEntity;
import com.medeasy.domain.routine_group.db.RoutineGroupEntity;
import com.medeasy.domain.routine_group.db.RoutineGroupRepository;
import com.medeasy.domain.routine_group_mapping.db.RoutineGroupMappingEntity;
import com.medeasy.domain.routine_group_mapping.db.RoutineGroupMappingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoutineGroupService {

    private final RoutineGroupRepository routineGroupRepository;
    private final RoutineGroupMappingRepository routineGroupMappingRepository;

    /**
     * 루틴 등록시 RoutineGroup과 Routine의 연관관계 매핑
     * */
    public void mappingRoutineGroup(String medicineId, List<RoutineEntity> routineEntities) {

        RoutineGroupEntity routineGroupEntity=RoutineGroupEntity.builder()
                .medicineId(medicineId)
                .build()
                ;

        routineGroupRepository.save(routineGroupEntity);

        List<RoutineGroupMappingEntity> routineGroupMappings= new ArrayList<>();
        routineEntities.forEach(routineEntity->{
            RoutineGroupMappingEntity routineGroupMapping=RoutineGroupMappingEntity.builder()
                    .routine(routineEntity)
                    .routineGroup(routineGroupEntity)
                    .build()
                    ;

            routineGroupMappings.add(routineGroupMapping);
        });

        routineGroupMappingRepository.saveAll(routineGroupMappings);
    }
}
