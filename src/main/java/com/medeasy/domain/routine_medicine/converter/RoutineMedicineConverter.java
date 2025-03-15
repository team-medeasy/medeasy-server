package com.medeasy.domain.routine_medicine.converter;

import com.medeasy.common.annotation.Converter;
import com.medeasy.domain.routine_medicine.db.RoutineMedicineEntity;
import com.medeasy.domain.routine_medicine.dto.RoutineMedicineDto;

@Converter
public class RoutineMedicineConverter {

    public RoutineMedicineDto toDto(RoutineMedicineEntity routineMedicineEntity) {
        return RoutineMedicineDto.builder()
                .routineMedicineId(routineMedicineEntity.getId())
                .nickname(routineMedicineEntity.getNickname())
                .medicineId(routineMedicineEntity.getMedicineId())
                .dose(routineMedicineEntity.getDose())
                .isTaken(routineMedicineEntity.getIsTaken())
                .build()
                ;
    }
}
