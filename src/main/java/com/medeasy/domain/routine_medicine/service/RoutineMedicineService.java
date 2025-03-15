package com.medeasy.domain.routine_medicine.service;

import com.medeasy.domain.routine_medicine.db.RoutineMedicineEntity;
import com.medeasy.domain.routine_medicine.db.RoutineMedicineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoutineMedicineService {

    private final RoutineMedicineRepository routineMedicineRepository;

    public void saveAll(List<RoutineMedicineEntity> routineMedicineEntities) {
        routineMedicineRepository.saveAll(routineMedicineEntities);
    }
}
