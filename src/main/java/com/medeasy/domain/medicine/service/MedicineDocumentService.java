package com.medeasy.domain.medicine.service;

import com.medeasy.common.error.MedicineErrorCode;
import com.medeasy.common.exception.ApiException;
import com.medeasy.domain.medicine.db.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicineDocumentService {
    private final MedicineSearchRepository medicineSearchRepository;
    private final MedicineSearchCustomRepository medicineSearchCustomRepository;


    public List<MedicineDocument> searchMedicineContainingNameWithColor(String medicineName, List<String> colors, List<String> shape, int size) {
        Pageable pageable = PageRequest.of(0, size);
        List<MedicineDocument> medicineDocuments=medicineSearchCustomRepository.findMedicineBySearching(medicineName, colors, shape, pageable);
        medicineDocuments.stream()
                .findAny()
                .orElseThrow(()-> new ApiException(MedicineErrorCode.NOT_FOUND_MEDICINE, "해당하는 약이 존재하지 않습니다. "+medicineName))
        ;
        return medicineDocuments;
    }

    public MedicineDocument findMedicineDocumentById(String id) {
        return medicineSearchRepository.findById(id).orElseThrow(() -> new ApiException(MedicineErrorCode.NOT_FOUND_MEDICINE));
    }

    /**
     * EDI_CODE를 통해 정확한 단일 약품 조회
     *
     * findMedicineByEdiCodeAndItemName로 대체
     * */
    @Deprecated
    public MedicineDocument findMedicineDocumentByEdiCode(String ediCode) {
        return medicineSearchCustomRepository.findByEdiCode(ediCode);
    }

    /**
     * EDI_CODE로 정확한 약품 검색이 어려울 때, 이름이 유사한 약품 검색
     * */
    public List<MedicineDocument> findMedicineByEdiCodeAndItemName(String ediCode, String itemName, int size) {
        Pageable pageable = PageRequest.of(0, size);
        List<MedicineDocument> medicineDocuments=medicineSearchCustomRepository.findMedicineByEdiCodeAndItemName(ediCode, itemName, pageable);
        medicineDocuments.stream()
                .findAny()
                .orElseThrow(() -> new ApiException(MedicineErrorCode.NOT_FOUND_MEDICINE, "일치하는 약이 존재하지 않습니다"));

        return medicineDocuments;
    }

    public List<MedicineDocument> findSimilarMedicineList(MedicineDocument medicineDocument, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        List<MedicineDocument> similarMedicineDocuments=medicineSearchCustomRepository.findSimilarMedicines(medicineDocument.getClassName(), medicineDocument.getIndications(), pageable) ;

        return similarMedicineDocuments;
    }
}
