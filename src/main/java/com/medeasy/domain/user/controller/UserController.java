package com.medeasy.domain.user.controller;

import com.medeasy.common.annotation.UserSession;
import com.medeasy.common.api.Api;
import com.medeasy.domain.user.dto.RoutineScheduleRequest;
import com.medeasy.domain.user.business.UserBusiness;
import com.medeasy.domain.user.dto.UserScheduleResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
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
}
