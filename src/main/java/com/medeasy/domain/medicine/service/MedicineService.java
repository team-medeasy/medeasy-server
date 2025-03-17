package com.medeasy.domain.medicine.service;

import com.medeasy.common.error.MedicineErrorCode;
import com.medeasy.common.exception.ApiException;
import com.medeasy.domain.medicine.converter.MedicineConverter;
import com.medeasy.domain.medicine.db.MedicineDocument;
import com.medeasy.domain.medicine.db.MedicineSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MedicineService {

    private final MedicineRepository medicineRepository;
    private final MedicineConverter medicineConverter;
    private final MedicineSearchRepository medicineSearchRepository;

    public MedicineEntity save(MedicineEntity medicineEntity) {
        return medicineRepository.save(medicineEntity);
    }

    public Page<MedicineEntity> searchMedicineByPaging(Pageable pageable) {
        return medicineRepository.findAll(pageable);
    }

    @Transactional
    public MedicineEntity saveMedicine(MedicineEntity medicineEntity) {
        MedicineEntity savedMedicineEntity=medicineRepository.save(medicineEntity);
        MedicineDocument medicineDocument=medicineConverter.toDocument(medicineEntity);
        medicineSearchRepository.save(medicineDocument);

        log.info("Saved medicine with id: {}", medicineEntity.getId());
        return savedMedicineEntity;
    }

    @Transactional
    public MedicineEntity updateMedicine(Long id, MedicineEntity updatedMedicine) {
        MedicineEntity existingMedicine = medicineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Medicine not found"));

        existingMedicine.setItemName(updatedMedicine.getItemName());
        existingMedicine.setEfficacy(updatedMedicine.getEfficacy());
        existingMedicine.setUseMethod(updatedMedicine.getUseMethod());
        existingMedicine.setAttention(updatedMedicine.getAttention());
        existingMedicine.setInteraction(updatedMedicine.getInteraction());
        existingMedicine.setSideEffect(updatedMedicine.getSideEffect());
        existingMedicine.setDepositMethod(updatedMedicine.getDepositMethod());

        MedicineEntity savedMedicine = medicineRepository.save(existingMedicine);

        // Elasticsearch에도 업데이트
        MedicineDocument medicineDocument = medicineConverter.toDocument(savedMedicine);

        medicineSearchRepository.save(medicineDocument);
        log.info("Updated medicine with id: {}", id);

        return savedMedicine;
    }

    @Transactional
    public void deleteMedicine(Long id) {
        MedicineEntity medicine = medicineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Medicine not found"));

        medicineRepository.delete(medicine);

        // Elasticsearch에서도 삭제
        medicineSearchRepository.deleteById(medicine.getId().toString());
        log.info("Deleted medicine with id: {}", id);
    }

    // TODO 저장시 동기화 조건 체크, 약 중복 발생시 처리 체크
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
                    log.info("Duplicate item_code ({}) detected.", entity.getItemCode());
                } else {
                    log.info("Duplicate item_code ({}) detected, but existing record not found.", entity.getItemCode());
                }
            }
        }
    }
    public MedicineEntity getMedicineById(Long id) {
        return medicineRepository.findById(id).orElseThrow(() -> new ApiException(MedicineErrorCode.NOT_FOUND_MEDICINE));
    }

    public MedicineEntity getMedicineByItemCode(String itemCode) {
        return medicineRepository.findByItemCode(itemCode).orElse(null);
    }
}
