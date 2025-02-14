package com.medeasy.domain.medicine.service;

import com.medeasy.domain.medicine.db.MedicineDocument;
import com.medeasy.domain.medicine.db.MedicineEntity;
import com.medeasy.domain.medicine.db.MedicineRepository;
import com.medeasy.domain.medicine.db.MedicineSearchRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicineSearchService {

    private final MedicineSearchRepository medicineSearchRepository;
    private final MedicineRepository medicineRepository; // 기존 JPA Repository

    @PostConstruct
    public void init() {
        indexAllMedicines();
    }

    public void indexAllMedicines() {
        List<MedicineEntity> medicines = medicineRepository.findAll();
        List<MedicineDocument> medicineDocuments = medicines.stream()
                .map(m -> MedicineDocument.builder()
                        .id(m.getId().toString())
                        .itemCode(m.getItemCode())
                        .entpName(m.getEntpName())
                        .itemName(m.getItemName())
                        .efficacy(m.getEfficacy())
                        .useMethod(m.getUseMethod())
                        .attention(m.getAttention())
                        .interaction(m.getInteraction())
                        .sideEffect(m.getSideEffect())
                        .depositMethod(m.getDepositMethod())
                        .build()
                ).toList();

        medicineSearchRepository.saveAll(medicineDocuments);
    }
}
