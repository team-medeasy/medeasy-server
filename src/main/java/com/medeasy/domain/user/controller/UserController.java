package com.medeasy.domain.user.controller;

import com.medeasy.common.annotation.UserSession;
import com.medeasy.common.api.Api;
import com.medeasy.domain.auth.business.AuthBusiness;
import com.medeasy.domain.routine.business.RoutineBusiness;
import com.medeasy.domain.routine_group.service.CurrentRoutineStrategy;
import com.medeasy.domain.routine_group.service.PastRoutineStrategy;
import com.medeasy.domain.routine_group.service.RoutineDateRangeStrategy;
import com.medeasy.domain.user.business.UserBusiness;
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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("user")
public class UserController {

    private final UserBusiness userBusiness;
    private final AuthBusiness authBusiness;
    private final RoutineBusiness routineBusiness;

    private final RoutineDateRangeStrategy currentRoutineStrategy;
    private final RoutineDateRangeStrategy pastRoutineStrategy;

    public UserController(
            UserBusiness userBusiness,
            AuthBusiness authBusiness,
            RoutineBusiness routineBusiness,
            @Qualifier(value = "currentRoutineStrategy") RoutineDateRangeStrategy currentRoutineStrategy,
            @Qualifier(value = "pastRoutineStrategy") RoutineDateRangeStrategy pastRoutineStrategy
    ) {
        this.userBusiness = userBusiness;
        this.authBusiness = authBusiness;
        this.routineBusiness = routineBusiness;
        this.currentRoutineStrategy = currentRoutineStrategy;
        this.pastRoutineStrategy = pastRoutineStrategy;
    }

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
        log.info("회원 정보 조회 완료 사용자: {}", userId);

        return Api.OK(response);
    }

    @Operation(summary = "현재 복용중인 약 루틴 정보 조회 API", description =
            """
                사용자 약 루틴 정보 조회 API:
                
                사용자가 복용하고 있는 간단한 약 정보와 복용기간, 복용량, 복용 주기를 반환한다.
            
            응답 값: 
            
            medicine_id: 약 ID
                                
            medicine_image: 약 이미지
                                
            medicine_name: 약 이름 
                                
            nickname: 약 루틴 등록 별칭 
                                
            entp_name: 제약사 이름 
                                
            class_name: 분류명
                                
            etc_otc_name: 전문의약품 여부 
                                
            routine_start_date: 약 복용 시작일
                                
            routine_end_date: 약 복용 종료일 
                                
            dose: 복용량 
                                
            schedule_size: 하루 복용 횟수 
                                
            day_of_weeks: 복용 주기 1~7 (월~일)
            """
    )
    @GetMapping("/medicines/current")
    public Api<Object> getCurrentUserMedicinesList(
            @Parameter(hidden = true)
            @UserSession Long userId
    ) {
        var response=routineBusiness.getRoutineList(userId, null, null, currentRoutineStrategy);

        return Api.OK(response);
    }

    @Operation(summary = "과거 복용하였던 약 루틴 정보 조회 API", description =
            """
                과거 복용하였던 약 루틴 정보 API:
                
                사용자가 복용하였던 있는 간단한 약 정보와 복용기간, 복용량, 복용 주기를 반환한다.
                
            요청 값:
            
            start_date: 조회 시작 날짜 (nullable)
            
            end_date: 조회 마지막 날짜 (nullable)
            
            응답 값: 
            
            medicine_id: 약 ID
                                
            medicine_image: 약 이미지
                                
            medicine_name: 약 이름 
                                
            nickname: 약 루틴 등록 별칭 
                                
            entp_name: 제약사 이름 
                                
            class_name: 분류명
                                
            etc_otc_name: 전문의약품 여부 
                                
            routine_start_date: 약 복용 시작일
                                
            routine_end_date: 약 복용 종료일 
                                
            dose: 복용량 
                                
            schedule_size: 하루 복용 횟수 
                                
            day_of_weeks: 복용 주기 1~7 (월~일)
            """
    )
    @GetMapping("/medicines/past")
    public Api<Object> getPastUserMedicinesList(
            @Parameter(hidden = true)
            @UserSession Long userId,
            @RequestParam(name = "start_date", required = false)LocalDate startDate,
            @RequestParam(name = "end_date", required = false)LocalDate endDate
    ) {
        var response=routineBusiness.getRoutineList(userId, startDate, endDate, pastRoutineStrategy);

        return Api.OK(response);
    }
}
