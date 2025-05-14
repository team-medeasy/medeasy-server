package com.medeasy.domain.medicine.service;

import com.medeasy.common.error.MedicineErrorCode;
import com.medeasy.common.exception.ApiException;
import com.medeasy.domain.medicine.db.DrugContraindicationsDocument;
import com.medeasy.domain.medicine.db.DrugContraindicationsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DrugContraindicationsService {

    private final DrugContraindicationsRepository drugContraindicationsRepository;

    public DrugContraindicationsDocument findByItemSeq(String itemSeq) {
        return drugContraindicationsRepository.findByItemSeq(itemSeq).orElseThrow(() -> new ApiException(MedicineErrorCode.NOT_FOUND_MEDICINE, "해당 의약품의 복용금기 내용이 존재하지 않습니다."));
    }
}
