package com.medeasy.domain.routine_group.converter;

import com.medeasy.common.annotation.Converter;
import com.medeasy.domain.routine.dto.RoutineRegisterRequest;
import com.medeasy.domain.routine_group.db.RoutineGroupEntity;
import lombok.RequiredArgsConstructor;

@Converter
@RequiredArgsConstructor
public class RoutineGroupConverter {

    public RoutineGroupEntity toEntityByRequest(RoutineRegisterRequest request) {
        return RoutineGroupEntity.builder()
                .dose(request.getDose())
                .nickname(request.getNickname())
                .medicineId(request.getMedicineId())
                .build()
                ;
    }
}
