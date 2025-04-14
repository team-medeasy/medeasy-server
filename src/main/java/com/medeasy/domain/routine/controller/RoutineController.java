package com.medeasy.domain.routine.controller;

import com.medeasy.common.annotation.UserSession;
import com.medeasy.common.api.Api;
import com.medeasy.domain.routine.business.RoutineBusiness;
import com.medeasy.domain.routine.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/routine")
@RequiredArgsConstructor
public class RoutineController {

    private final RoutineBusiness routineBusiness;

    @Operation(summary = "루틴 등록 v2", description =
            """
                루틴 등록 API: 요일과, 약을 먹는 시기를 입력하면, 입력한 날짜를 기준으로 루틴들을 등록
                
                1. 시작 날짜와, 시작 스케줄을 입력하지 않았을 때 -> 기존 루틴 등록과 동일 
                
                오늘 날짜의 시간이 지나지 않은 스케줄부터 차례로 루틴 등록 
                
                2. 시작 날짜가 과거일 때 -> 시작 날짜 nullable = false, 시작 스케줄 nullable = true (null일 경우 가장 빠른 시간의 스케줄)
                
                과거인 루틴들에 대해서 생성 후 자동 복용 체크, 이후에 복용할 루틴에 대해서는 기존과 동일 
                
                3. 시작 날짜가 미래일 때 -> 시작 날짜 nullable = false, 시작 스케줄 nullable = true (null일 경우 가장 빠른 시간의 스케줄)
                
                미래 날짜의 정해진 시간부터 차례로 루틴 생성 
                
                추가 4/13
                
                기존 루틴 날짜 계산 방식 변경: 선택한 요일에 해당하는 날짜 -> 날짜 간격
            
            필드 설명:
            
            medicine_id: 루틴에 등록하고자 하는 약의 id
            
            dose: 1회 복용량
             
            total_quantity: 총 약의 개수
             
            day_of_weeks: 1(월요일)~7(일요일) 복용하고자 하는 숫자 배열 입력
             
            user_schedule_ids: 약을 먹고자 하는 사용자 스케줄 배열 입력
            
            routine_start_date: 약을 먹기 시작하는 날짜, null 일시 오늘 날짜 
            
            start_user_schedule_id: 약 복용 시작 스케줄, null 일시 제일 빠른 스케줄
            
            interval_days: 약 복용 날짜 간격, 1-> 매일 7-> 일주일에 한번 
            
            마지막 업데이트 4/13
             
            """
    )
    @PostMapping("")
    public Api<Object> registerRoutine(
            @Parameter(hidden = true) @UserSession Long userId,
            @Valid
            @RequestBody RoutineRegisterRequest routineRegisterRequest
    ) {
        routineBusiness.registerRoutine(userId, routineRegisterRequest);
        log.info("루틴 등록 완료 사용자: {}", userId);
        return Api.OK(null);
    }

    @Operation(summary = "루틴 리스트 등록 v2", description =
            """
                루틴 리스트 등록 API: 단일 루틴 정보를 리스트로 받아 한번에 저장 
            
            마지막 업데이트 3/18 -> 최적화 작업 완료 
             
            """
    )
    @PostMapping("/list")
    public Api<Object> registerRoutineList(
            @Parameter(hidden = true) @UserSession Long userId,
            @Valid
            @RequestBody List<RoutineRegisterRequest> routinesRegisterRequest
    ) {
        routineBusiness.registerRoutineList(userId, routinesRegisterRequest);
        log.info("루틴 리스트 등록 완료 사용자: {}", userId);

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
        log.info("루틴 조회 완료 사용자: {}", userId);

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
            @Parameter(hidden = true) @UserSession Long userId,
            @RequestParam("routine_id")
            @Parameter(description = "체크할 루틴 id", required = true)
            Long routineId,
            @RequestParam("is_taken")
            @Parameter(description = "약 복용 여부", required = true)
            Boolean isTaken
    ) {
        RoutineCheckResponse response=routineBusiness.checkRoutine(routineId, isTaken);

        log.info("루틴 체크 완료 사용자: {}", userId);
        return Api.OK(response);
    }

    @Operation(summary = "처방전 OCR 루틴 등록 v2", description =
            """
                처방전 사진을 통한 루틴 등록 API:
                
                1. 처방전의 글자 데이터를 추출
                
                2. 의약품에 해당하는 데이터만 가지고 gemini api 가공 
                
                3. 추천 루틴 등록 정보를 제공 (약 정보, 루틴 시간)
                        
            요청 방법:
            
            MultipartRequest를 통해 이미지 파일 전송 
            
            응답 값: 
            
            medicine_id: 약 식별 id 
            
            medicine_name: 약 이름, 루틴을 등록할 때 기본 별명에 삽입 
            
            dose, total_quantity: 1회 복용량, 총 복용량 
            
            user_schedules: 추천하는 사용자 복용 시간 대 정보 
            
            - user_schedule_id: 사용자 스케줄 식별 id
                
            - name: 스케줄 이름 
                
            - take_time: 약 복용 시간 
            
            - is_recommended: 추천 스케줄 유무 
            
            day_of_weeks: 추천하는 약 복용 요일
            
            마지막 업데이트 4/11
            """
    )
    @PostMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            path = "/prescription"
    )
    public Api<List<RoutinePrescriptionResponse>> registerRoutineByPrescription(
            @Parameter(hidden = true) @UserSession Long userId,
            @RequestPart("image") MultipartFile image
    ) {
        var response=routineBusiness.registerRoutineByPrescription(userId, image);
        log.info("처방전 루틴 분석 완료: {}", userId);
        return Api.OK(response);
    }

    @Operation(summary = "단일 루틴 삭제 api v2", description =
            """
                단일 루틴 삭제 api 
            
            요청: 
            
            PathVariable로 삭제하려는 routine_medicine_id값 지정 
            """
    )
    @DeleteMapping("/{routine_id}")
    public Api<Object> deleteRoutine(
            @Parameter(hidden = true) @UserSession Long userId,
            @Parameter(description = "삭제 하려는 루틴 id", required = true)
            @PathVariable("routine_id") Long routineId
    ) {
        routineBusiness.deleteRoutine(userId, routineId);
        log.info("루틴 삭제 완료 사용자: {}", userId);
        return Api.OK(null);
    }

    @Operation(summary = "전체 루틴 삭제 api", description =
            """
                전체 루틴 삭제 api 
            
            요청: 
            
            routine id 를 입력하여 같은 그룹의 루틴들도 전체 삭제 
            """
    )
    @DeleteMapping("/group/{routine_id}")
    public Api<Object> deleteGroupRoutine(
            @Parameter(hidden = true) @UserSession Long userId,
            @Parameter(description = "삭제 하려는 루틴 id", required = true)
            @PathVariable("routine_id") Long routineId
    ) {
        routineBusiness.deleteGroupRoutine(userId, routineId);
        log.info("루틴 삭제 완료 사용자: {}", userId);
        return Api.OK(null);
    }

    @PatchMapping("")
    public Api<Object> updateRoutineMedicine(
            @Parameter(hidden = true) @UserSession Long userId,
            @Valid @RequestBody RoutineUpdateRequest request
    ) {
        routineBusiness.updateRoutine(userId, request);

        return null;
    }
}
