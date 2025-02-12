package com.medeasy.domain.medicine.service;

import com.medeasy.domain.medicine.db.MedicineEntity;
import com.medeasy.domain.medicine.db.MedicineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public void saveAllWithDuplicate(List<MedicineEntity> entities) {
        for (MedicineEntity entity : entities) {
            try {
                medicineRepository.save(entity);
            } catch (DataIntegrityViolationException e) {
                Optional<MedicineEntity> existingEntityOpt = medicineRepository.findByItemCode(entity.getItemCode());

                if (existingEntityOpt.isPresent()) {
                    MedicineEntity existingEntity = existingEntityOpt.get();

                    // 기존 데이터 업데이트 (필요한 필드만 업데이트)
                    existingEntity.setEfficacy(entity.getEfficacy());
                    existingEntity.setUseMethod(entity.getUseMethod());
                    existingEntity.setAttention(entity.getAttention());
                    existingEntity.setInteraction(entity.getInteraction());
                    existingEntity.setSideEffect(entity.getSideEffect());
                    existingEntity.setDepositMethod(entity.getDepositMethod());

                    // 업데이트 저장
                    medicineRepository.save(existingEntity);
                    System.out.println("Duplicate item_code (" + entity.getItemCode() + ") detected. Updating existing record.");
                } else {
                    System.out.println("Duplicate item_code (" + entity.getItemCode() + ") detected, but existing record not found.");
                }
            }
        }
    }
}
