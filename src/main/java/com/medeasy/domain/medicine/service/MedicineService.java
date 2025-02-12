package com.medeasy.domain.medicine.service;

import com.medeasy.domain.medicine.db.MedicineEntity;
import com.medeasy.domain.medicine.db.MedicineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicineService {

    private final MedicineRepository medicineRepository;

    public MedicineEntity save(MedicineEntity medicineEntity) {
        return medicineRepository.save(medicineEntity);
    }

    public Page<MedicineEntity> searchMedicineByPaging(Pageable pageable) {
        return medicineRepository.findAll(pageable);
    }

    public void saveAll(List<MedicineEntity> entities) {
        medicineRepository.saveAll(entities);
    }
}
