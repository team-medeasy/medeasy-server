package com.medeasy.domain.medicine.service;

import com.medeasy.common.error.MedicineErrorCode;
import com.medeasy.common.exception.ApiException;
import com.medeasy.domain.medicine.db.MedicineDocument;
import com.medeasy.domain.medicine.db.MedicineEntity;
import com.medeasy.domain.medicine.db.MedicineRepository;
import com.medeasy.domain.medicine.db.MedicineSearchRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicineDocumentService {

    private static final Logger log = LoggerFactory.getLogger(MedicineDocumentService.class);
    private final MedicineSearchRepository medicineSearchRepository;
    private final MedicineRepository medicineRepository; // 기존 JPA Repository

    // 애플리케이션 실행시 elasticsearch repository, repo 동기화 작업
//    @PostConstruct
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
                        .shape(m.getShape())
                        .color(m.getColor())
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

    // TODO 검색한 약이 존재하지 않을 경우 크롤링 고려
    public List<MedicineDocument> searchMedicineContainingName(String medicineName, int size) {
        log.info("약 검색 {}", medicineName);

        Pageable pageable = PageRequest.of(0, size);
        List<MedicineDocument> medicineDocuments=medicineSearchRepository.findByItemNameContaining(medicineName, pageable);
        medicineDocuments.stream()
                .findAny()
                .orElseThrow(()-> new ApiException(MedicineErrorCode.NOT_FOUND_MEDICINE, "해당하는 약이 존재하지 않습니다. "+medicineName))
                ;
        return medicineDocuments;
    }

    public List<MedicineDocument> searchMedicineContainingNameWithColor(String medicineName, List<String> colors, int size) {
        Pageable pageable = PageRequest.of(0, size);
        List<MedicineDocument> medicineDocuments=medicineSearchRepository.findByItemNameAndColors(medicineName, colors, pageable);
        medicineDocuments.stream()
                .findAny()
                .orElseThrow(()-> new ApiException(MedicineErrorCode.NOT_FOUND_MEDICINE, "해당하는 약이 존재하지 않습니다. "+medicineName))
        ;
        return medicineDocuments;
    }
}
