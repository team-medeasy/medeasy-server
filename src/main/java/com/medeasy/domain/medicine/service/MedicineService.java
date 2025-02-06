package com.medeasy.medicine.service;

import com.medeasy.medicine.db.MedicineEntity;
import com.medeasy.medicine.db.MedicineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MedicineService {

    private final MedicineRepository medicineRepository;

    public MedicineEntity save(MedicineEntity medicineEntity) {
        return medicineRepository.save(medicineEntity);
    }
}
