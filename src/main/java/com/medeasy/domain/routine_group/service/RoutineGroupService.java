package com.medeasy.domain.routine_group.service;

import com.medeasy.common.error.ErrorCode;
import com.medeasy.common.error.RoutineErrorCode;
import com.medeasy.common.exception.ApiException;
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
    public void mappingRoutineGroup(RoutineGroupEntity routineGroupEntity, List<RoutineEntity> routineEntities) {
        routineGroupRepository.save(routineGroupEntity);

        routineEntities.forEach(r -> r.setRoutineGroup(routineGroupEntity));
    }

    public RoutineGroupEntity findByRoutineIdAndUserId(Long routineId, Long userId) {
        return routineGroupRepository.findByRoutineIdAndUserId(routineId, userId).orElseThrow(()->new ApiException(ErrorCode.BAD_REQEUST, "루틴 그룹이 존재하지 않습니다."));
    }

    public RoutineGroupEntity findRoutineGroupContainsRoutineIdByUserId(Long userId, Long routineId) {
        return routineGroupRepository.findRoutineGroupContainsRoutineIdByUserId(userId, routineId)
                .orElseThrow(()->new ApiException(ErrorCode.BAD_REQEUST, "루틴 그룹이 존재하지 않습니다"));
    }

    public void delete(RoutineGroupEntity routineGroupEntity) {
        routineGroupRepository.delete(routineGroupEntity);
    }

    public Long findUserRoutineGroupByMedicineId(Long userId, String medicineId) {
        return routineGroupRepository.findFirstRoutineIdByUserIdAndMedicineId(userId, medicineId)
                .orElseThrow(()-> new ApiException(RoutineErrorCode.NOT_FOUND_ROUTINE, "해당 약을 복용하고 있는 루틴 그룹이 존재하지 않습니다."));
    }

    public List<RoutineGroupEntity> findRoutineGroupInIds(List<Long> routineGroupIds) {
        return routineGroupRepository.findByIdIn(routineGroupIds);
    }
}
