package com.medeasy.domain.interested_medicine.service;

import com.medeasy.domain.interested_medicine.db.InterestedMedicineEntity;
import com.medeasy.domain.interested_medicine.db.InterestedMedicineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InterestedMedicineService {
    private final InterestedMedicineRepository interestedMedicineRepository;

    public void saveInterestedMedicine(InterestedMedicineEntity entity) {
        interestedMedicineRepository.save(entity);
    }
}
