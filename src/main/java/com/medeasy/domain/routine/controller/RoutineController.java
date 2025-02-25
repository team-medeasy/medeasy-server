package com.medeasy.domain.routine.controller;

import com.medeasy.common.annotation.UserSession;
import com.medeasy.common.api.Api;
import com.medeasy.domain.routine.business.RoutineBusiness;
import com.medeasy.domain.routine.dto.RoutineCheckResponse;
import com.medeasy.domain.routine.dto.RoutineGroupDto;
import com.medeasy.domain.routine.dto.RoutineGroupResponse;
import com.medeasy.domain.routine.dto.RoutineRegisterRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/routine")
@RequiredArgsConstructor
public class RoutineController {

    private final RoutineBusiness routineBusiness;

    @Operation(summary = "루틴 등록", description =
            """
            루틴 등록 API: 요일과, 약을 먹는 시기를 입력하면, 오늘 날짜를 기준으로 루틴들을 등록
            
            필드 설명:
            
            medicine_id: 루틴에 등록하고자 하는 약의 id
            
            dose: 1회 복용량
             
            total_quantity: 총 약의 개수
             
            day_of_weeks: 1(월요일)~7(일요일) 복용하고자 하는 숫자 배열 입력
             
            types: "MORNING", "LUNCH", "DINNER", "BEDTIME" 약을 복용하는 시기 입력
             
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

    @Operation(summary = "특정 날짜 루틴 조회", description =
            """
            루틴 조회 API: 특정 날짜의 사용자 루틴 리스트 조회 
            
            PathVariable을 통해 date 정보를 요청에 포함
            
            형식: 2025-02-25
             
            """
    )
    @GetMapping("/{date}")
    public Api<List<RoutineGroupResponse>> getRoutineListByDate(
            @Parameter(hidden = true) @UserSession Long userId,
            @PathVariable("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        var response=routineBusiness.getRoutineListByDate(userId, date);

        return Api.OK(response);
    }

    @GetMapping("test/{date}")
    public String test(
            @PathVariable("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        routineBusiness.test(date);

        return "ok";
    }


    @Operation(summary = "루틴 복용 여부 체크", description =
            """
            루틴 복용 여부 체크 API: 특정 routine의 복용 여부 체크
            
            routine_id와 복용 여부 'true' or 'false'를 query sting 으로 요청 
            
            반환값: routine_id, beforeIsTaken, afterIsTaken
            """
    )
    @PatchMapping("/check")
    public Api<Object> checkRoutine(
            @RequestParam("routine_id")
            @Parameter(description = "체크할 루틴 id", required = true)
            Long routineId,
            @RequestParam("is_taken")
            @Parameter(description = "약 복용 여부", required = true)
            Boolean isTaken
    ) {
        RoutineCheckResponse response=routineBusiness.checkRoutine(routineId, isTaken);
        return Api.OK(response);
    }
}
