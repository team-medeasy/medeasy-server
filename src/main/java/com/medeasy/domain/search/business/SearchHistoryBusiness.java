package com.medeasy.domain.search.business;

import com.medeasy.common.annotation.Business;
import com.medeasy.domain.medicine.service.MedicineDocumentService;
import lombok.RequiredArgsConstructor;

@Business
@RequiredArgsConstructor
public class SearchHistoryBusiness {

    private final MedicineDocumentService medicineDocumentService;

    public void saveSearchKeyword(String userId, String medicineName) {
            medicineDocumentService.saveSearchKeyword(userId.toString(), medicineName);
    }
}
