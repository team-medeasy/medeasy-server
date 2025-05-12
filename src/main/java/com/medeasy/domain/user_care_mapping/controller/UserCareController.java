package com.medeasy.domain.user_care_mapping.controller;

import com.medeasy.common.annotation.UserSession;
import com.medeasy.common.api.Api;
import com.medeasy.common.error.UserErrorCode;
import com.medeasy.common.exception.ApiException;
import com.medeasy.domain.auth.dto.CareAuthCodeResponse;
import com.medeasy.domain.routine.business.RoutineBusiness;
import com.medeasy.domain.routine.dto.RoutineGroupDto;
import com.medeasy.domain.user.business.UserBusiness;
import com.medeasy.domain.user.dto.RegisterCareRequest;
import com.medeasy.domain.user.dto.RegisterCareResponse;
import com.medeasy.domain.user.dto.UserListResponse;
import com.medeasy.domain.user_care_mapping.business.UserCareBusiness;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/care")
public class UserCareController {

    private final UserBusiness userBusiness;
    private final RoutineBusiness routineBusiness;
    private final UserCareBusiness userCareBusiness;

    @Operation(summary = "루틴 관리 대상(피보호자) 등록 API", description =
            """
                루틴 관리 대상(피보호자) 등록 API:
                
                보호 대상을 등록하여 루틴 관리 및 관련 알림을 제공받는다.
                
                피보호자의 인증 코드를 입력하여 요청을 보낸다. 
                
            요청 값: 
            
            auth_code: 인증 토큰 
           
            응답 값: 
                
            care_giver_id: 보호자 id 
            
            care_receiver_id: 피보호자 id
            
            registered_at: 관계가 등록된 시간 
            """
    )
    @PostMapping("/receiver")
    public Api<RegisterCareResponse> registerCareReceiver(
            @Parameter(hidden = true) @UserSession Long userId,
            @Valid @RequestBody RegisterCareRequest request
    ) {
        try {
            var response=userBusiness.registerCareReceiver(userId, request);
            return Api.OK(response);

        }catch (DataIntegrityViolationException e){
            throw new ApiException(UserErrorCode.DUPLICATE_CARE_ERROR);
        }
    }

    @Operation(summary = "보호 대상 삭제", description =
            """
            보호 대상 삭제 API:
            
            care_receiver_id를 입력하여 보호 대상 해제
            """
    )
    @DeleteMapping("/{receiver_id}")
    public Api<Object> deleteUserReceiver(
            @Parameter(hidden = true) @UserSession Long userId,
            @PathVariable(name = "receiver_id") Long receiverId
    ) {
        userBusiness.deleteUserReceiver(userId, receiverId);
        log.info("보호 대상 해제 완료: {}", userId);

        return Api.OK(null);
    }


    @Operation(summary = "복약 관리 목록 조회 API", description =
            """
                사용자 포함 피보호자 리스트 제공 API
                
                사용자 전환을 위해 사용자 리스트를 제공한다.
                
            응답 값:
            
            name: 사용자 또는 피보호자 이름
            
            email: 피보호자 이메일 
            
            user_id: 사용자 식별자 
            
            tag: 내 계정 또는 피보호자 
            """
    )
    @GetMapping("/list")
    public Api<Object> getUserCareList(
            @Parameter(hidden = true) @UserSession Long userId
    ) {
        List<UserListResponse> response=userBusiness.getUserCareList(userId);
        return Api.OK(response);
    }

    @Operation(summary = "보호 대상 인증 코드 발급 API", description =
            """
                보호 대상 인증 코드 발급 API:
                
                보호 대상을 등록하기 위한 인증 코드 
                
                보호 대상이 인증 코드를 발급 받은 후, 보호자가 인증 코드를 입력하여 등록한다.
           
            응답 값: 
                
            auth_code: 보호 등록 인증 코드 
            """
    )
    @PostMapping("/auth-code")
    public CareAuthCodeResponse generateCareAuthCode(
            @Parameter(hidden = true) @UserSession Long userId
    ){
        return userBusiness.generateCareAuthCode(userId);
    }


    @Operation(summary = "복약 케어 루틴 조회 API", description =
            """
                복약 케어 루틴 조회 API
                
                복약 케어 대상의 루틴을 조회한다.
                
            응답 값:
            
            name: 사용자 또는 피보호자 이름
            
            email: 피보호자 이메일 
            
            user_id: 사용자 식별자 
            
            tag: 내 계정 또는 피보호자 
            """
    )
    @GetMapping("/routine/{user_id}")
    public Api<List<RoutineGroupDto>> getCareReceiverRoutines(
            @Parameter(hidden = true) @UserSession Long userId,
            @RequestParam(name = "user_id") Long careReceiverUserId,
            @Parameter(example = "2025-03-16") @RequestParam(name = "start_date", required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(example = "2025-03-20") @RequestParam(name = "end_date", required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {


        List<RoutineGroupDto> response= userCareBusiness.getCareReceiverRoutineListByDate(userId, careReceiverUserId, startDate, endDate);


                routineBusiness.getRoutineListByDate(careReceiverUserId, startDate, endDate);
        return Api.OK(response);
    }

}
