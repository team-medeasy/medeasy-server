package com.medeasy.domain.medicine.service;

import com.medeasy.common.error.MedicineErrorCode;
import com.medeasy.common.exception.ApiException;
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
public class MedicineDocumentService {

    private final MedicineSearchRepository medicineSearchRepository;
    private final MedicineRepository medicineRepository; // 기존 JPA Repository

    // 애플리케이션 실행시 elasticsearch repository, repo 동기화 작업
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
                        .openAt(m.getOpenAt())
                        .updateAt(m.getUpdateAt())
                        .imageUrl(m.getImageUrl())
                        .bizrno(m.getBizrno())
                        .build()
                ).toList();

        medicineSearchRepository.saveAll(medicineDocuments);
    }

    public List<MedicineDocument> searchMedicineContainingName(String medicineName) {
        List<MedicineDocument> medicineDocuments=medicineSearchRepository.findByItemNameContaining(medicineName);
        medicineDocuments.stream()
                .findAny()
                .orElseThrow(()-> new ApiException(MedicineErrorCode.NOT_FOUND_MEDICINE))
                ;
        return medicineDocuments;
    }
}
