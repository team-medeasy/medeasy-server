package com.medeasy.domain.medicine.business;

import com.medeasy.common.annotation.Business;
import com.medeasy.common.logging.SaveLogToTxt;
import com.medeasy.domain.medicine.converter.MedicineConverter;
import com.medeasy.domain.medicine.db.MedicineDocument;
import com.medeasy.domain.medicine.db.MedicineEntity;
import com.medeasy.domain.medicine.db.MedicineRepository;
import com.medeasy.domain.medicine.dto.MedicineRequest;
import com.medeasy.domain.medicine.dto.MedicineResponse;
import com.medeasy.domain.medicine.dto.MedicineUpdateRequest;
import com.medeasy.domain.medicine.service.MedicineDocumentService;
import com.medeasy.domain.medicine.service.MedicineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;


@Slf4j
@Business
@RequiredArgsConstructor
public class MedicineBusiness {

    private final MedicineService medicineService;
    private final MedicineDocumentService medicineDocumentService;
    private final MedicineConverter medicineConverter;
    private final SaveLogToTxt saveLogToTxt;
    private final MedicineRepository medicineRepository;

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

    public List<MedicineResponse> searchMedicines(String medicineName, int size) {
        List<MedicineDocument> medicineDocuments=medicineDocumentService.searchMedicineContainingName(medicineName, size);

        return medicineDocuments.stream()
                .map(medicineConverter::toResponseWithDocument).toList();
    }

    public void updateMedicines(List<MedicineUpdateRequest> requests) {
        requests.stream().forEach(request -> {
                    MedicineEntity medicineEntity=medicineService.getMedicineByItemCode(request.getItemSeq());

                    if (medicineEntity == null) {
                        log.warn("Medicine not found for itemCode: {}", request.getItemSeq());
                        return;  // 이 요청은 건너뛰고 다음 요청으로 진행
                    }

                    String color = combineColors(request.getColorClass1(), request.getColorClass2());

                    medicineEntity.setShape(request.getShape());
                    medicineEntity.setColor(color);

                    medicineRepository.save(medicineEntity);
                });
    }

    public String combineColors(String color1, String color2) {
        // 컬러 값 결합 로직
        String color = null;

        if (color1 != null && !color1.isEmpty() && color2 != null && !color2.isEmpty()) {
            color = color1 + ", " + color2;
        } else if (color1 != null && !color1.isEmpty()) {
            color = color1;
        } else if (color2 != null && !color2.isEmpty()) {
            color = color2;
        }

        return color;
    }

    public List<MedicineResponse> searchMedicinesWithColor(String medicineName, List<String> colors, int size) {
        List<MedicineDocument> medicineDocuments=medicineDocumentService.searchMedicineContainingNameWithColor(medicineName, colors, size);

        return medicineDocuments.stream()
                .map(medicineConverter::toResponseWithDocument).toList();

    }
}
