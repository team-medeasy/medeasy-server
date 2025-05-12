package com.medeasy.domain.user_care_mapping.controller;

import com.medeasy.common.annotation.UserSession;
import com.medeasy.common.api.Api;
import com.medeasy.common.error.UserErrorCode;
import com.medeasy.common.exception.ApiException;
import com.medeasy.domain.user.business.UserBusiness;
import com.medeasy.domain.user.dto.RegisterCareRequest;
import com.medeasy.domain.user.dto.RegisterCareResponse;
import com.medeasy.domain.user.dto.UserListResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/care")
public class UserCareController {

    private final UserBusiness userBusiness;

    @Operation(summary = "루틴 관리 대상(피보호자) 등록 API", description =
            """
                루틴 관리 대상(피보호자) 등록 API:
                
                루틴 정보와 알림을 받고자 하는 피보호자를 등록
            
                피보호자의 이메일과 비밀번호를 입력하여 관계 검증 
           
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

    @Operation(summary = "루틴 관리 보호자 등록 API", description =
            """
                루틴 관리 보호자 등록 API:
                
                루틴 정보와 알림을 제공할 보호자 등록
            
                보호자 이메일과 비밀번호를 입력하여 관계 검증 
           
            응답 값: 
                
            care_giver_id: 보호자 id 
            
            care_receiver_id: 피보호자 id
            
            registered_at: 관계가 등록된 시간 
            """
    )
    @PostMapping("/provider")
    public Api<RegisterCareResponse> registerCareProvider(
            @Parameter(hidden = true) @UserSession Long userId,
            @Valid @RequestBody RegisterCareRequest request
    ) {
        try {
            var response=userBusiness.registerCareProvider(userId, request);
            log.info("보호자 등록 완료: {}", userId);
            return Api.OK(response);

        }catch (DataIntegrityViolationException e){
            throw new ApiException(UserErrorCode.DUPLICATE_CARE_ERROR);
        }
    }

    @Operation(summary = "사용자 피보호자 리스트 조회", description =
            """
            사용자 피보호자 리스트 조회 API:
            
            사용자가 등록한 관리 대상 리스트를 보여준다.
            """
    )
    @GetMapping("/receivers")
    public Api<Object> getUserCareProviders(
            @Parameter(hidden = true) @UserSession Long userId
    ) {
        var response=userBusiness.getUserCareReceivers(userId);
        log.info("피보호자 리스트 조회 완료: {}", userId);

        return Api.OK(response);
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
}
