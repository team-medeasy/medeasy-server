package com.medeasy.domain.medicine.service;

import com.medeasy.common.api.Api;
import com.medeasy.common.error.MedicineErrorCode;
import com.medeasy.common.exception.ApiException;
import com.medeasy.domain.medicine.db.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
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
}
