package com.medeasy.domain.routine.converter;

import com.medeasy.common.annotation.Converter;
import com.medeasy.domain.routine.db.RoutineEntity;
import com.medeasy.domain.routine.dto.RoutineDto;
import com.medeasy.domain.routine.dto.RoutineGroupDto;
import com.medeasy.domain.routine.dto.RoutineRegisterRequest;
import com.medeasy.domain.routine.dto.RoutineGroupResponse;

import java.util.ArrayList;
import java.util.List;

@Converter
public class RoutineConverter {

    public RoutineEntity toEntity(RoutineRegisterRequest request) {

        return null;
    }

    public RoutineGroupResponse toGroupResponse(RoutineGroupDto routineGroupDto) {

        List<RoutineGroupResponse.RoutineMedicineResponse> medicines = new ArrayList<>();

        for(RoutineDto routineDto : routineGroupDto.getRoutines()) {
            var medicine= RoutineGroupResponse.RoutineMedicineResponse.builder()
                    .routineId(routineDto.getRoutineId())
                    .medicineName(routineDto.getNickname())
                    .isTaken(routineDto.getIsTaken())
                    .build()
            ;
            medicines.add(medicine);
        }

        return RoutineGroupResponse.builder()
                .takeTime(routineGroupDto.getTakeTime())
                .takeDate(routineGroupDto.getRoutines().getFirst().getTakeDate())
                .type(routineGroupDto.getRoutines().getFirst().getType())
                .medicines(medicines)
                .build()
                ;
    }
}
