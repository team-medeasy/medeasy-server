package com.medeasy.domain.user.business;

import com.medeasy.common.annotation.Business;
import com.medeasy.common.error.ErrorCode;
import com.medeasy.common.error.UserErrorCode;
import com.medeasy.common.exception.ApiException;
import com.medeasy.domain.auth.util.TokenHelperIfs;
import com.medeasy.domain.routine.db.RoutineEntity;
import com.medeasy.domain.routine.service.RoutineService;
import com.medeasy.domain.user.dto.*;
import com.medeasy.domain.user.db.UserEntity;
import com.medeasy.domain.user.service.UserConverter;
import com.medeasy.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Business
@RequiredArgsConstructor
public class UserBusiness {

    private final UserService userService;
    private final UserConverter userConverter;
    private final RoutineService routineService;
    private final PasswordEncoder passwordEncoder;

    /**
     * request의 null이 아닌 수정사항만 사용자의 정보에서 업데이트
     * */
    @Transactional
    public UserScheduleResponse updateRoutineSchedule(Long userId, RoutineScheduleRequest request) {
        UserEntity userEntity=userService.getUserById(userId);

//        Optional.ofNullable(request.getMorningTime())
//                .ifPresent(userEntity::setMorning);
//
//        Optional.ofNullable(request.getLunchTime())
//                .ifPresent(userEntity::setLunch);
//
//        Optional.ofNullable(request.getDinnerTime())
//                .ifPresent(userEntity::setDinner);
//
//        Optional.ofNullable(request.getBedTime())
//                .ifPresent(userEntity::setBedTime);
//
//        // 트랜잭션이 끝나는 시점에 영속성 컨텍스트가 변경 사항을 알아서 커밋
//
//        return UserScheduleResponse.builder()
//                .morning(userEntity.getMorning())
//                .lunch(userEntity.getLunch())
//                .dinner(userEntity.getDinner())
//                .bedTime(userEntity.getBedTime())
//                .build()
//                ;
        return null;
    }

    public UserScheduleResponse getRoutineSchedule(Long userId) {
        UserEntity userEntity=userService.getUserById(userId);

//        return UserScheduleResponse.builder()
//                .morning(userEntity.getMorning())
//                .lunch(userEntity.getLunch())
//                .dinner(userEntity.getDinner())
//                .bedTime(userEntity.getBedTime())
//                .build()
//                ;
        return null;
    }

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
    public UserMedicinesResponse getUserMedicinesCount(Long userId) {
        UserEntity userEntity=userService.getUserById(userId);
        List<Long> userMedicinesIds = routineService.getRoutinesByUserId(userId);

        return UserMedicinesResponse.builder()
                .medicineCount(userMedicinesIds.size())
                .medicineIds(userMedicinesIds)
                .build()
                ;
    }

    public void unregisterUser(Long userId, String password) {
        UserEntity userEntity=userService.getUserById(userId);

        if (!passwordEncoder.matches(password, userEntity.getPassword())) {
            throw new ApiException(UserErrorCode.INVALID_PASSWORD);
        }

        userService.deleteUser(userId);
    }

    public UserResponse getUserInfo(Long userId) {
        UserEntity userEntity=userService.getUserById(userId);

        return userConverter.toResponse(userEntity);
    }
}
