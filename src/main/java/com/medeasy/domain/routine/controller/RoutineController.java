package com.medeasy.domain.routine.controller;

import com.medeasy.common.annotation.UserSession;
import com.medeasy.common.api.Api;
import com.medeasy.domain.routine.business.RoutineBusiness;
import com.medeasy.domain.routine.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/routine")
@RequiredArgsConstructor
public class RoutineController {

    private final RoutineBusiness routineBusiness;

    @Operation(summary = "루틴 등록 v2", description =
            """
            루틴 등록 API: 요일과, 약을 먹는 시기를 입력하면, 오늘 날짜를 기준으로 루틴들을 등록
            
            필드 설명:
            
            medicine_id: 루틴에 등록하고자 하는 약의 id
            
            dose: 1회 복용량
             
            total_quantity: 총 약의 개수
             
            day_of_weeks: 1(월요일)~7(일요일) 복용하고자 하는 숫자 배열 입력
             
            types: "MORNING", "LUNCH", "DINNER", "BEDTIME" 약을 복용하는 시기 입력
            
            
            마지막 업데이트 3/16
             
            """
    )
    @PostMapping("")
    public Api<Object> registerRoutine(
            @Parameter(hidden = true) @UserSession Long userId,
            @Valid
            @RequestBody RoutineRegisterRequest routineRegisterRequest
    ) {
        routineBusiness.registerRoutine(userId, routineRegisterRequest);

        return Api.OK(null);
    }

    @Operation(summary = "날짜 범위의 루틴 조회 v2", description =
            """
            루틴 조회 API: 특정 날짜의 사용자 루틴 리스트 조회 
            
            RequestParam을 통해 조회 시작 날짜와 마지막 날짜를 입력 
            
            형식: 2025-02-25
             
            마지막 업데이트 3/16
            """
    )
    @GetMapping("")
    public Api<List<RoutineGroupDto>> getRoutineListByDate(
            @Parameter(hidden = true) @UserSession Long userId,
            @Parameter(example = "2025-03-16") @RequestParam(name = "start_date", required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(example = "2025-03-20") @RequestParam(name = "end_date", required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        var response=routineBusiness.getRoutineListByDate(userId, startDate, endDate);

        return Api.OK(response);
    }

    @Operation(summary = "루틴 복용 여부 체크 v2", description =
            """
            루틴 복용 여부 체크 API: 특정 routine의 복용 여부 체크
            
            routine_medicine_id와 복용 여부 'true' or 'false'를 query sting 으로 요청 
            
            반환값: routine_medicine_id, beforeIsTaken, afterIsTaken
            
            마지막 업데이트 3/16
            """
    )
    @PatchMapping("/check")
    public Api<Object> checkRoutine(
            @RequestParam("routine_medicine_id")
            @Parameter(description = "체크할 루틴 id", required = true)
            Long routineMedicineId,
            @RequestParam("is_taken")
            @Parameter(description = "약 복용 여부", required = true)
            Boolean isTaken
    ) {
        RoutineCheckResponse response=routineBusiness.checkRoutine(routineMedicineId, isTaken);
        return Api.OK(response);
    }

    @Operation(summary = "처방전 OCR 루틴 등록(임시)", description =
            """
            처방전 사진을 통한 루틴 등록 API:
            
            1. 처방전의 글자 데이터를 추출
            
            2. 의약품에 해당하는 데이터만 가지고 gemini api 가공 
            
            3. 루틴 등록 알고리즘을 통해 사용자 루틴등록  
                        
            요청 방법:
            
            MultipartRequest를 통해 이미지 파일 전송 
            
            응답 값: 
            상태 코드, 메시지만 응답 
            """
    )
    @PostMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            path = "/prescription"
    )
    public Api<Object> registerRoutineByPrescription(
            @Parameter(hidden = true) @UserSession Long userId,
            @RequestPart("image") MultipartFile image
    ) {
        routineBusiness.registerRoutineByPrescription(userId, image);
        return Api.OK(null);
    }

    @Operation(summary = "단일 루틴 삭제 api", description =
            """
            단일 루틴 삭제 api 
            
            요청: 
            
            PathVariable로 삭제하려는 routine_id값 지정 
            """
    )
    @DeleteMapping("/{routine_id}")
    public Api<Object> deleteRoutine(
            @Parameter(hidden = true)
            @UserSession Long userId,
            @Parameter(description = "삭제 하려는 루틴 id", required = true)
            @PathVariable("routine_id") Long routineId
    ) {
        routineBusiness.deleteRoutine(userId, routineId);
        return Api.OK(null);
    }
}
