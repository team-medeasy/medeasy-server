package com.medeasy.domain.user.business;

import com.medeasy.common.annotation.Business;
import com.medeasy.common.error.ErrorCode;
import com.medeasy.common.error.SchedulerError;
import com.medeasy.common.error.TokenErrorCode;
import com.medeasy.common.error.UserErrorCode;
import com.medeasy.common.exception.ApiException;
import com.medeasy.domain.auth.business.AuthBusiness;
import com.medeasy.domain.auth.dto.CareAuthCodeResponse;
import com.medeasy.domain.auth.dto.TokenResponse;
import com.medeasy.domain.auth.service.AuthCodeService;
import com.medeasy.domain.auth.util.JwtTokenHelper;
import com.medeasy.domain.auth.util.TokenHelperIfs;
import com.medeasy.domain.routine.service.RoutineService;
import com.medeasy.domain.user.dto.*;
import com.medeasy.domain.user.db.UserEntity;
import com.medeasy.domain.user.service.UserConverter;
import com.medeasy.domain.user.service.UserService;
import com.medeasy.domain.user_care_mapping.converter.UserCareMappingConverter;
import com.medeasy.domain.user_care_mapping.db.UserCareMappingEntity;
import com.medeasy.domain.user_care_mapping.db.UserCareMappingRepository;
import com.medeasy.domain.user_care_mapping.dto.CareReceiverResponse;
import com.medeasy.domain.user_care_mapping.service.UserCareMappingService;
import com.medeasy.domain.user_schedule.converter.UserScheduleConverter;
import com.medeasy.domain.user_schedule.db.UserScheduleEntity;
import com.medeasy.domain.user_schedule.dto.UserScheduleDto;
import com.medeasy.domain.user_schedule.dto.UserScheduleRegisterRequest;
import com.medeasy.domain.user_schedule.dto.UserScheduleResponse;
import com.medeasy.domain.user_schedule.dto.UserScheduleUpdateRequest;
import com.medeasy.domain.user_schedule.service.UserScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.Token;
import org.apache.catalina.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Slf4j
@Business
@RequiredArgsConstructor
public class UserBusiness {

    private final UserService userService;
    private final UserConverter userConverter;
    private final PasswordEncoder passwordEncoder;
    private final UserScheduleConverter userScheduleConverter;
    private final UserScheduleService userScheduleService;
    private final RoutineService routineService;
    private final AuthBusiness authBusiness;
    private final AuthCodeService authCodeService;

    private final UserCareMappingService userCareMappingService;
    private final UserCareMappingConverter userCareMappingConverter;
    private final UserCareMappingRepository userCareMappingRepository;
    private final JwtTokenHelper jwtTokenHelper;


    /**
     * request의 null이 아닌 수정사항만 사용자의 정보에서 업데이트
     * */
    @Transactional
    public UserScheduleDto updateRoutineSchedule(Long userId, UserScheduleUpdateRequest request) {
        UserEntity userEntity=userService.getUserByIdToFetchJoin(userId);

        UserScheduleEntity userScheduleEntity=userEntity.getUserSchedules().stream()
                .filter(schedule -> schedule.getId().equals(request.getUserScheduleId()))
                .findFirst()
                .orElseThrow(()->new ApiException(SchedulerError.NOT_FOUND));

        // 요청에 포함된 값만 업데이트
        if (request.getScheduleName() != null) {
            userScheduleEntity.setName(request.getScheduleName());
        }
        if (request.getTakeTime() != null) {
            userScheduleEntity.setTakeTime(request.getTakeTime());
        }

        return userScheduleConverter.toDto(userScheduleEntity);
    }

    /**
     * 사용자 스케줄 반환 메서드
     * */
    @Transactional
    public List<UserScheduleResponse> getRoutineSchedule(Long userId) {
        UserEntity userEntity=userService.getUserByIdToFetchJoin(userId);

        return userEntity.getUserSchedules().stream().map(userScheduleConverter::toResponse).toList();
    }

    /**
     * 사용자 가입 일수 반환 메서드
     * */
    @Transactional
    public UserUsageDaysResponse getServiceUsageDays(Long userId) {

        UserEntity userEntity=userService.getUserById(userId);

        log.info("user registered days: {}", userEntity.getRegisteredAt());

        Date registeredAt=userEntity.getRegisteredAt();

        LocalDate registeredDate = Instant.ofEpochMilli(registeredAt.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        LocalDate now = LocalDate.now();

        long serviceDays = ChronoUnit.DAYS.between(registeredDate, now)+1L;

        return UserUsageDaysResponse.builder()
                .userId(userId)
                .usageDays(serviceDays)
                .build()
                ;
    }

    /**
     * 복용하고 있는 약의 개수를 구하려면
     * 현재 입장에서는 사용자 루틴 리스트를 불러오고
     * 순차로 돌면서 중복되지 않는 약들을 가져와야한다...
     *
     * 수시로 가져오는 간단한 값인데 계산 과정이 너무 복잡
     * 1. user 테이블에 반정규화
     * 2. routine 위에 User medicine 테이블 추가
     *
     * 1번의 경우 루틴을 등록할 때마다 카운트, 루틴이 만료될 때 디스카운트 해야하는데
     *
     * */
    @Transactional
    public UserMedicinesResponse getUserMedicinesCount(Long userId) {
        UserEntity userEntity=userService.getUserById(userId);
        List<String> medicineIds = routineService.getDistinctRoutineByUserId(userId);

        return UserMedicinesResponse.builder()
                .medicineCount(medicineIds.size())
                .medicineIds(medicineIds)
                .build()
                ;
    }

    /**
     * 사용자 회원탈퇴 메서드
     * */
    @Transactional
    public void unregisterUser(Long userId, String refreshToken) {
        UserEntity userEntity=userService.getUserById(userId);
        String storedRefreshToken=jwtTokenHelper.getRefreshTokenByUserId(userId.toString());

        if(!refreshToken.equals(storedRefreshToken)){
            throw new ApiException(TokenErrorCode.INVALID_TOKEN, "Refresh token does not match");
        }

        userService.deleteUser(userId);
    }

    /**
     * 사용자 단순 정보 조회 메서드
     * */
    @Transactional
    public UserResponse getUserInfo(Long userId) {
        UserEntity userEntity=userService.getUserById(userId);

        return userConverter.toResponse(userEntity);
    }

    /**
     * 사용자 스케줄 등록 메서드
     * */
    @Transactional
    public void registerRoutineSchedule(Long userId, UserScheduleRegisterRequest request) {
        UserEntity userEntity=userService.getUserByIdToFetchJoin(userId);

        UserScheduleEntity userScheduleEntity=UserScheduleEntity.builder()
                .name(request.getScheduleName())
                .takeTime(request.getTakeTime())
                .user(userEntity)
                .build()
                ;

        userScheduleService.save(userScheduleEntity);
    }

    @Transactional
    public void deleteRoutineSchedule(Long userId, Long userScheduleId) {
        UserScheduleEntity userScheduleEntity=userScheduleService.getUserScheduleByFetchJoin(userScheduleId);

        userScheduleEntity.getRoutine().stream()
                .findAny()
                .ifPresent(routine-> {throw new ApiException(SchedulerError.FOREIGN_KEY_CONSTRAINT);} );

        userScheduleService.deleteById(userScheduleId);
    }

    @Transactional
    public RegisterCareResponse registerCareReceiver(Long userId, RegisterCareRequest request) {
        Long careReceiverId=authCodeService.getUserIdByAuthCode(request.getAuthCode());
        if (careReceiverId == null) {
            throw new ApiException(ErrorCode.AUTH_ERROR, "잘못된 코드입니다. 다시 확인해주세요.");
        }

        UserEntity careReceiverUserEntity = userService.getUserById(careReceiverId);
        UserEntity careGiverUserEntity = userService.getUserById(userId);

        UserCareMappingEntity userCareMappingEntity=userCareMappingConverter.registerCareRelation(careGiverUserEntity, careReceiverUserEntity);
        UserCareMappingEntity newUserCareMappingEntity=userCareMappingService.save(userCareMappingEntity);

        return RegisterCareResponse.builder()
                .careGiverId(newUserCareMappingEntity.getCareProvider().getId())
                .careReceiverId(newUserCareMappingEntity.getCareReceiver().getId())
                .registeredAt(newUserCareMappingEntity.getRegisteredAt())
                .build()
                ;
    }

    @Transactional
    public List<CareReceiverResponse> getUserCareReceivers(Long userId) {
        List<UserCareMappingEntity> careReceivers=userCareMappingService.findAllCareReceivers(userId);

        return careReceivers.stream().map(userCareMappingConverter::toCareReceiverResponse).toList();
    }

    @Transactional
    public void deleteUserReceiver(Long userId, Long receiverId) {
        userCareMappingService.deleteCareReceiver(userId, receiverId);
    }

    @Transactional
    public void updateUserName(Long userId, String name) {
        UserEntity userEntity=userService.getUserById(userId);
        userEntity.setName(name);
    }

    @Transactional
    public List<UserListResponse> getUserCareList(Long userId) {
        UserEntity userEntity = userService.getUserWithCareReceivers(userId);

        List<UserEntity> careReceivers = Optional.ofNullable(userEntity.getCareReceivers())
                .orElse(Collections.emptyList())
                .stream()
                .map(UserCareMappingEntity::getCareReceiver)
                .toList();

        List<UserListResponse> result = new ArrayList<>(1 + careReceivers.size());

        result.add(UserListResponse.builder()
                .userId(userEntity.getId())
                .name(userEntity.getName())
                .email(userEntity.getEmail())
                .tag("내 계정")
                .build()
        );

        result.addAll(
                careReceivers.stream()
                        .map(r -> UserListResponse.builder()
                                .userId(r.getId())
                                .name(r.getName())
                                .email(r.getEmail())
                                .tag("피보호자")
                                .build()
                        )
                        .toList()
        );

        return result;
    }

    @Transactional
    public TokenResponse loginByCareReceiver(Long userId, Long careReceiverUserId) {
        UserEntity userEntity = userService.getUserWithCareReceivers(userId);
        List<Long> careReceiversIds = Optional.ofNullable(userEntity.getCareReceivers())
                .orElse(Collections.emptyList())
                .stream()
                .map(UserCareMappingEntity::getCareReceiver)
                .map(UserEntity::getId)
                .toList();

        if(!careReceiversIds.contains(careReceiverUserId)){
            throw new ApiException(UserErrorCode.NOT_FOUND_CARE_RECEIVER);
        }

        return authBusiness.issueToken(careReceiverUserId);
    }

    public CareAuthCodeResponse generateCareAuthCode(Long userId) {
        String authCode=authCodeService.generateAuthCode(userId.toString());

        return new CareAuthCodeResponse(authCode);
    }
}
