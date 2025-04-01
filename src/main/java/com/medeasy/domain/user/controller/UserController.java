package com.medeasy.domain.user.controller;

import com.medeasy.common.annotation.UserSession;
import com.medeasy.common.api.Api;
import com.medeasy.common.error.UserErrorCode;
import com.medeasy.common.exception.ApiException;
import com.medeasy.domain.auth.business.AuthBusiness;
import com.medeasy.domain.user.business.UserBusiness;
import com.medeasy.domain.user.dto.RegisterCareReceiverRequest;
import com.medeasy.domain.user.dto.RegisterCareResponse;
import com.medeasy.domain.user.dto.UserDeleteRequest;
import com.medeasy.domain.user.dto.UserUsageDaysResponse;
import com.medeasy.domain.user_schedule.dto.UserScheduleDto;
import com.medeasy.domain.user_schedule.dto.UserScheduleRegisterRequest;
import com.medeasy.domain.user_schedule.dto.UserScheduleUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("user")
public class UserController {

    private final UserBusiness userBusiness;
    private final AuthBusiness authBusiness;

    @Operation(summary = "사용자 루틴 스케줄 수정 api v2", description =
            """
                사용자 루틴 스케줄 수정 API:
                
                루틴을 등록할 떼 사용되는 사용자의 스케줄 정보 변경
            
            request_body 설명: 
            
            - user_schedule_id: 사용자 스케줄 식별 ID
            
            - schedule_name: 스케줄 별명 
            
            - take_time: 약 복용 시간  
                        
            요청 방법:
           
            수정 예정 데이터만 response_body에 담아 요청 
            
            ex) 스케줄 별명만 변경하고 싶은 경우 아래와 같이 요청  
            
            ```json
            {
                "user_schedule_id": 1,
                "name": "아침 식사 후"
            }
            ```
            
            마지막 업데이트: 3/17
            """
    )
    @PatchMapping("/schedule/update")
    public Api<Object> updateRoutineSchedule(
            @Parameter(hidden = true)
            @UserSession Long userId,
            @Valid
            @RequestBody UserScheduleUpdateRequest request
    ){
        UserScheduleDto response=userBusiness.updateRoutineSchedule(userId, request);
        log.info("스케줄 업데이트 완료 사용자: {}", userId);

        return Api.OK(response);
    }

    @Operation(summary = "사용자 루틴 스케줄 추가 api", description =
            """
                사용자 루틴 스케줄 추가 API:
                
                루틴을 등록할 떼 사용되는 사용자의 스케줄 추가
            
            요청 값 설명: 
            
            - name: 스케줄 이름
            
            - take_time: 복용 시간
            
            마지막 업데이트: 3/18
            """
    )
    @PostMapping("/schedule")
    public Api<Object> registerRoutineSchedule(
            @Parameter(hidden = true)
            @UserSession Long userId,
            @Valid @RequestBody UserScheduleRegisterRequest request
            ) {
        userBusiness.registerRoutineSchedule(userId, request);
        log.info("스케줄 추가 완료 사용자: {}", userId);

        return Api.OK(null);
    }

    @Operation(summary = "사용자 루틴 스케줄 삭제 api", description =
            """
                사용자 루틴 스케줄 삭제 API:
                
                루틴을 등록할 떼 사용되는 사용자의 스케줄 삭제
            
            요청 값 설명: 
            
            - take_time: 복용 시간
            
            마지막 업데이트: 3/18
            """
    )
    @DeleteMapping("/{user_schedule_id}/schedule")
    public Api<Object> deleteRoutineSchedule(
            @Parameter(hidden = true)
            @UserSession Long userId,
            @PathVariable(name = "user_schedule_id") Long userScheduleId
    ) {
        userBusiness.deleteRoutineSchedule(userId, userScheduleId);

        return Api.OK(null);
    }

    @Operation(summary = "사용자 루틴 스케줄 조회 api v2", description =
            """
                사용자 루틴 스케줄 조회 API:
                
                루틴을 등록할 떼 사용되는 사용자의 스케줄 시간 조회
            
            응답 값 설명: 
            
            - user_schedule_id: 사용자 스케줄 식별 ID
            
            - name: 스케줄 이름
            
            - take_time: 복용 시간
            
            마지막 업데이트: 3/17
            """
    )
    @GetMapping("/schedule")
    public Api<Object> getRoutineSchedule(
            @Parameter(hidden = true)
            @UserSession Long userId
    ) {
        List<UserScheduleDto> response=userBusiness.getRoutineSchedule(userId);
        log.info("스케줄 조회 완료 사용자: {}", userId);

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
        log.info("서비스 이용날짜 반환 완료 사용자: {}", userId);

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
        log.info("회원 탈퇴 완료 사용자: {}", userId);

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
        log.info("회원 정보 조회 완료 사용자: {}", userId);

        return Api.OK(response);
    }

    @Operation(summary = "루틴 관리 대상 등록 API", description =
            """
                루틴 관리 대상 등록 API:
                
                루틴 정보와 알림을 받고자 하는 피보호자를 등록
            
                피보호자의 이메일과 비밀번호를 입력하여 관계 검증 
           
            응답 값: 
                
            care_giver_id: 보호자 id 
            
            care_receiver_id: 피보호자 id
            
            registered_at: 관계가 등록된 시간 
            """
    )
    @PostMapping("/care_receiver")
    public Api<RegisterCareResponse> registerCareReceiver(
            @Parameter(hidden = true) @UserSession Long userId,
            @Valid @RequestBody RegisterCareReceiverRequest request
    ) {
        try {
            var response=userBusiness.registerCareReceiver(userId, request);
            return Api.OK(response);

        }catch (DataIntegrityViolationException e){
            throw new ApiException(UserErrorCode.DUPLICATE_CARE_ERROR);
        }
    }
}
