package com.medeasy.domain.medicine.controller;

import com.medeasy.common.annotation.UserSession;
import com.medeasy.common.api.Api;
import com.medeasy.domain.medicine.business.MedicineBusiness;
import com.medeasy.domain.medicine.db.MedicineColor;
import com.medeasy.domain.medicine.db.MedicineShape;
import com.medeasy.domain.medicine.dto.MedicineRequest;
import com.medeasy.domain.medicine.dto.MedicineResponse;
import com.medeasy.domain.medicine.dto.MedicineUpdateRequest;
import com.medeasy.domain.search.business.SearchHistoryBusiness;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/medicine")
@RequiredArgsConstructor
public class MedicineController {

    private final MedicineBusiness medicineBusiness;
    private final SearchHistoryBusiness searchHistoryBusiness;

    // 약 검색 색상 필터링
    @GetMapping("/search")
    @Operation(summary = "약 검색 API v2", description =
            """
            약 검색 API:
            
            약 이름, 색상, 모양 조건 선택하여 검색 
                
            약 색상, 모양 여러개 조건 선택시 ex) 빨강, 노랑 
            
            빨간약, 노랸약, 빨간노란약 출력 
            
            이외의 조건은 지속적으로 추가 예정 
            """)
    public Api<List<MedicineResponse>> searchMedicinesWithColor(
            @Parameter(hidden = true) @UserSession Long userId,
            @RequestParam(value = "name", required = false)
            @Parameter(description = "약 이름 키워드 (nullable)", required = false)
            String name,

            @RequestParam(value = "colors", required = false)
            @Parameter(description = "약 색상 필터링 (nullable)", required = false)
            List<MedicineColor> colors,

            @RequestParam(value = "shape", required = false)
            @Parameter(description = "약 모양 필터링 (nullable)", required = false)
            List<MedicineShape> shapes,

            @RequestParam(value = "size", defaultValue = "10", required = false)
            @Parameter(description = "불러올 데이터 개수 (default: 10)", required = false)
            int size
    ) {
        // 검색 기록 저장
        if (name != null){
            searchHistoryBusiness.saveSearchKeyword(userId.toString(), name);
        }

        // 약 검색
        List<MedicineResponse> medicineResponses= medicineBusiness.searchMedicinesWithColor(userId, name, colors, shapes, size);

        return Api.OK(medicineResponses);
    }

}
