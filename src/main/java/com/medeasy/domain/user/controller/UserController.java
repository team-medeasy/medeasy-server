package com.medeasy.domain.user.controller;

import com.medeasy.common.annotation.UserSession;
import com.medeasy.common.api.Api;
import com.medeasy.domain.user.dto.RoutineScheduleRequest;
import com.medeasy.domain.user.business.UserBusiness;
import com.medeasy.domain.user.dto.UserDeleteRequest;
import com.medeasy.domain.user.dto.UserScheduleResponse;
import com.medeasy.domain.user.dto.UserUsageDaysResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("user")
public class UserController {

    private final UserBusiness userBusiness;

    @Operation(summary = "사용자 루틴 스케줄 변경 api", description =
            """
            사용자 루틴 스케줄 변경 API:
            
            루틴을 등록할 떼 사용되는 사용자의 스케줄 시간 변경
            
            request_body 설명: 
            
            morning_time(아침 시간), lunch_time(점심 시간), dinner_time(저녁 시간), bed_time(취침 시간) 
                        
            요청 방법:
           
            변경하고자 하는 시간대만 request_body 에 담아 요청  
            """
    )
    @PatchMapping("/schedule/update")
    public Api<Object> updateRoutineSchedule(
            @Parameter(hidden = true)
            @UserSession Long userId,
            @Valid
            @RequestBody RoutineScheduleRequest request
    ){
        UserScheduleResponse response=userBusiness.updateRoutineSchedule(userId, request);

        return Api.OK(response);
    }

    @Operation(summary = "사용자 루틴 스케줄 조회 api", description =
            """
            사용자 루틴 스케줄 조회 API:
            
            루틴을 등록할 떼 사용되는 사용자의 스케줄 시간 조회
            
            응답 값 설명: 
            
            morning(아침 시간), lunch(점심 시간), dinner(저녁 시간), bed_time(취침 시간) 
            """
    )
    @GetMapping("/schdule")
    public Api<Object> getRoutineSchedule(
            @Parameter(hidden = true)
            @UserSession Long userId
    ) {
        UserScheduleResponse response=userBusiness.getRoutineSchedule(userId);

        return Api.OK(response);
    }

    @Operation(summary = "서비스 이용 날짜 반환", description =
            """
            서비스 이용 날짜 반환 API:
            
            내 정보에 표시되는 서비스 이용 날짜 반환
            
            가입날짜로부터 오늘날짜를 빼서 계산 
            """
    )
    @GetMapping("/usage-days")
    public Api<Object> getServiceUsageDays(
            @Parameter(hidden = true)
            @UserSession Long userId
    ) {
        UserUsageDaysResponse response=userBusiness.getServiceUsageDays(userId);

        return Api.OK(response);
    }

    @Operation(summary = "사용자 복용하고 있는 약 개수 반환", description =
            """
            사용자 복용하고 있는 약 개수 반환 API:
            
            사용자 총 복용 약품 수, 복용 중인 약 id 리스트 반환
            """
    )
    @GetMapping("/medicine/count")
    public Api<Object> getUserMedicinesCount(
            @Parameter(hidden = true)
            @UserSession Long userId
    ) {
        var response=userBusiness.getUserMedicinesCount(userId);

        return Api.OK(response);
    }

    @Operation(summary = "회원 탈퇴 API", description =
            """
            사용자 회원 탈퇴 API:
            
            사용자 비밀번호를 추가로 입력받아
            
            인증이 완료되면 사용자 삭제 
            """
    )
    @PostMapping("")
    public Api<Object> deleteUser(
            @Parameter(hidden = true)
            @UserSession Long userId,
            @Valid@RequestBody UserDeleteRequest request
    ) {
        userBusiness.unregisterUser(userId, request.getPassword());

        return Api.OK(null);
    }

    @Operation(summary = "회원 정보 조회 API", description =
            """
            사용자 회원 조회 API:
            
            사용자 기본 정보 반환하는 API
            """
    )
    @GetMapping("")
    public Api<Object> getUser(
            @Parameter(hidden = true)
            @UserSession Long userId
    ) {
        var response=userBusiness.getUserInfo(userId);

        return Api.OK(response);
    }
}
