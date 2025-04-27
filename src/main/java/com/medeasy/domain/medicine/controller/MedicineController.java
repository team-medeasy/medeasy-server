package com.medeasy.domain.medicine.controller;

import com.medeasy.common.annotation.UserSession;
import com.medeasy.common.api.Api;
import com.medeasy.domain.medicine.business.MedicineBusiness;
import com.medeasy.domain.medicine.db.MedicineColor;
import com.medeasy.domain.medicine.db.MedicineShape;
import com.medeasy.domain.medicine.dto.MedicineResponse;
import com.medeasy.domain.medicine.dto.MedicineSimpleDto;
import com.medeasy.domain.search.business.SearchHistoryBusiness;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
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

            @RequestParam(value = "page", defaultValue = "0", required = false)
            @Parameter(description = "페이지 번호(default: 0)", required = false)
            int page,

            @RequestParam(value = "size", defaultValue = "10", required = false)
            @Parameter(description = "불러올 데이터 개수 (default: 10)", required = false)
            int size
    ) {
        // 검색 기록 저장
        if (name != null){
            searchHistoryBusiness.saveSearchKeyword(userId.toString(), name);
        }

        // 약 검색
        List<MedicineResponse> medicineResponses= medicineBusiness.searchMedicinesWithColor(userId, name, colors, shapes, page, size);

        return Api.OK(medicineResponses);
    }

    @GetMapping("/similar")
    @Operation(summary = "유사한 약 리스트 조회 API", description =
                    """
                    유사 약 리스트 조회 API:
                                
                    medicine_id에 해당하는 약과 유사한 약 리스트 출력 
                    """)
    public Api<List<MedicineSimpleDto>> getSimilarMedicineList(
            @Parameter(hidden = true) @UserSession Long userId,
            @RequestParam(name = "medicine_id") String medicineId,
            @RequestParam(name = "page") int page,
            @RequestParam(name = "size") int size
    ) {
        var response=medicineBusiness.getSimilarMedicineList(medicineId, page, size);

        return Api.OK(response);
    }

    @GetMapping("/medicine_id/{medicine_id}")
    @Operation(summary = "medicine id를 통한 약 조회", description =
            """
                약 조회 API
                
                medicine_id를 통한 약 데이터 조회
            """)
    public Api<MedicineResponse> getMedicineById(
            @Parameter(hidden = true) @UserSession Long userId,
            @PathVariable("medicine_id") String medicineId
    ) {
        MedicineResponse response=medicineBusiness.getMedicineById(medicineId);

        return Api.OK(response);
    }

    @GetMapping("/item_seq/{item_seq}")
    @Operation(summary = "medicine item_seq를 통한 약 조회", description =
            """
                약 조회 API
                
                item_seq를 통한 약 데이터 조회
            """)
    public Api<MedicineResponse> getMedicineByItemSeq(
            @Parameter(hidden = true) @UserSession Long userId,
            @PathVariable("item_seq") String itemSeq
    ) {
        MedicineResponse response=medicineBusiness.getMedicineByItemSeq(itemSeq);

        return Api.OK(response);
    }

    @GetMapping("/list")
    @Operation(summary = "약 리스트 조회", description =
            """
                약 리스트 조회 API
                
                medicine id list를 통한 약 정보 리스트 조회 API
            """)
    public Api<List<MedicineResponse>> getMedicineListByIds(
            @Parameter(hidden = true) @UserSession Long userId,
            @RequestParam("medicine_ids") List<String> medicineIds
    ) {
        List<MedicineResponse> response=medicineBusiness.getMedicineListByIds(medicineIds);

        return Api.OK(response);
    }

    @Operation(summary = "약 음성 파일 조회 api", description =
            """
                약 정보 음성 파일 url 조회  
                
                medicine_id에 해당하는 약의 효능, 복용 방법, 주의사항, 부작용에 대해서 음성 정보 제공하는 파일 url을 반환한다.
                
                일반적으로 약 정보 조회시 음성파일 URL 필드(audio_url) 제공하지만 존재하지 않을 경우에 이 api로 요청하여 음성 파일 생성과 url 반환  
            """)
    @GetMapping("/{medicine_id}/audio-url")
    public Api<String> getMedicineInfoMp3File(
            @Parameter(hidden = true) @UserSession Long userId,
            @PathVariable(name = "medicine_id") String medicineId
    ) {
        var response=medicineBusiness.getMedicineInfoMp3FileUri(medicineId);

        return Api.OK(response);
    }
}
