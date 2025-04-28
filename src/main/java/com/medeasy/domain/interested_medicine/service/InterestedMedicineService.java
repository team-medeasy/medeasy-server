package com.medeasy.domain.interested_medicine.service;

import com.medeasy.domain.interested_medicine.db.InterestedMedicineEntity;
import com.medeasy.domain.interested_medicine.db.InterestedMedicineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InterestedMedicineService {
    private final InterestedMedicineRepository interestedMedicineRepository;

    public void saveInterestedMedicine(InterestedMedicineEntity entity) {
        interestedMedicineRepository.save(entity);
    }

    public Optional<InterestedMedicineEntity> getOptionalInterestedMedicine(Long userId, String medicineId) {
        return interestedMedicineRepository.findByUserIdAndMedicineId(userId, medicineId);
    }

    public void deleteInterestedMedicine(InterestedMedicineEntity entity) {
        interestedMedicineRepository.delete(entity);
    }
}
