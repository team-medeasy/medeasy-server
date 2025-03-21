package com.medeasy.domain.medicine.business;

import com.medeasy.common.annotation.Business;
import com.medeasy.common.logging.SaveLogToTxt;
import com.medeasy.domain.medicine.converter.MedicineConverter;
import com.medeasy.domain.medicine.db.*;
import com.medeasy.domain.medicine.dto.MedicineRequest;
import com.medeasy.domain.medicine.dto.MedicineResponse;
import com.medeasy.domain.medicine.dto.MedicineSimpleDto;
import com.medeasy.domain.medicine.dto.MedicineUpdateRequest;
import com.medeasy.domain.medicine.service.MedicineDocumentService;
import com.medeasy.domain.medicine.service.MedicineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;


@Slf4j
@Business
@RequiredArgsConstructor
public class MedicineBusiness {

    private final MedicineDocumentService medicineDocumentService;
    private final MedicineConverter medicineConverter;

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

    /**
     * 메인 검색 로직
     * */
    public List<MedicineResponse> searchMedicinesWithColor(Long userId, String medicineName, List<MedicineColor> enumColors, List<MedicineShape> enumShapes, int size) {
        List<String> colors= (enumColors != null && !enumColors.isEmpty()) ? enumColors.stream().map(MedicineColor::getColor).toList() : null;
        log.info("medicine business color 변환: {}", colors);

        List<String> shapes= (enumShapes != null && !enumShapes.isEmpty()) ? enumShapes.stream().map(MedicineShape::getShape).toList() : null;
        log.info("medicine business shape 변환: {}", shapes);

        List<MedicineDocument> medicineDocuments=medicineDocumentService.searchMedicineContainingNameWithColor(medicineName, colors, shapes, size);

        return medicineDocuments.stream().map(medicineConverter::toResponseWithDocument).toList();
    }

    public List<MedicineSimpleDto> getSimilarMedicineList(String medicineId, int page, int size) {
        MedicineDocument medicineDocument=medicineDocumentService.findMedicineDocumentById(medicineId);

        List<MedicineDocument> medicineDocuments=medicineDocumentService.findSimilarMedicineList(medicineDocument, page, size);
        // 유사한 약 중 검색 대상 약 제외
        medicineDocuments.removeFirst();

        return medicineDocuments.stream().map(medicineConverter::toSimpleResponseWithDocument).toList();
    }
}
