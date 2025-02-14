package com.medeasy.domain.medicine.business;

import com.medeasy.common.annotation.Business;
import com.medeasy.common.logging.SaveLogToTxt;
import com.medeasy.domain.medicine.converter.MedicineConverter;
import com.medeasy.domain.medicine.db.MedicineDocument;
import com.medeasy.domain.medicine.db.MedicineEntity;
import com.medeasy.domain.medicine.dto.MedicineRequest;
import com.medeasy.domain.medicine.dto.MedicineResponse;
import com.medeasy.domain.medicine.service.MedicineDocumentService;
import com.medeasy.domain.medicine.service.MedicineService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.util.List;

import static java.util.stream.Collectors.toList;

@Business
@RequiredArgsConstructor
public class MedicineBusiness {

    private final MedicineService medicineService;
    private final MedicineDocumentService medicineDocumentService;
    private final MedicineConverter medicineConverter;
    private final SaveLogToTxt saveLogToTxt;

    public Page<MedicineResponse> searchMedicineByPaging(Pageable pageable) {
        Page<MedicineEntity> medicineEntities=medicineService.searchMedicineByPaging(pageable);

        Page<MedicineResponse> response=medicineConverter.toResponse(medicineEntities);

        return response;
    }

    public void saveMedicines(List<MedicineRequest> requests) {
        List<MedicineEntity> entities = requests.stream()
                        .map(medicineConverter::toEntity)
                        .toList();

        medicineService.saveAllWithDuplicate(entities);

        // 저장된 약품의 기록을 txt 파일에 남김
        saveLogToTxt.saveItemSeqToFile(requests);
    }

    public List<MedicineResponse> searchMedicines(String medicineName) {
        List<MedicineDocument> medicineDocuments=medicineDocumentService.searchMedicineContainingName(medicineName);

        return medicineDocuments.stream()
                .map(medicineConverter::toResponseWithDocument).toList();
    }
}
