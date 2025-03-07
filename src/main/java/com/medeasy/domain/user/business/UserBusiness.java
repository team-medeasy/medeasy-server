package com.medeasy.domain.user.business;

import com.medeasy.common.annotation.Business;
import com.medeasy.domain.user.dto.RoutineScheduleRequest;
import com.medeasy.domain.user.db.UserEntity;
import com.medeasy.domain.user.dto.UserScheduleResponse;
import com.medeasy.domain.user.dto.UserUsageDaysResponse;
import com.medeasy.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

@Slf4j
@Business
@RequiredArgsConstructor
public class UserBusiness {

    private final UserService userService;

    /**
     * request의 null이 아닌 수정사항만 사용자의 정보에서 업데이트
     * */
    @Transactional
    public UserScheduleResponse updateRoutineSchedule(Long userId, RoutineScheduleRequest request) {
        UserEntity userEntity=userService.getUserById(userId);

        Optional.ofNullable(request.getMorningTime())
                .ifPresent(userEntity::setMorning);

        Optional.ofNullable(request.getLunchTime())
                .ifPresent(userEntity::setLunch);

        Optional.ofNullable(request.getDinnerTime())
                .ifPresent(userEntity::setDinner);

        Optional.ofNullable(request.getBedTime())
                .ifPresent(userEntity::setBedTime);

        // 트랜잭션이 끝나는 시점에 영속성 컨텍스트가 변경 사항을 알아서 커밋

        return UserScheduleResponse.builder()
                .morning(userEntity.getMorning())
                .lunch(userEntity.getLunch())
                .dinner(userEntity.getDinner())
                .bedTime(userEntity.getBedTime())
                .build()
                ;
    }

    public UserScheduleResponse getRoutineSchedule(Long userId) {
        UserEntity userEntity=userService.getUserById(userId);

        return UserScheduleResponse.builder()
                .morning(userEntity.getMorning())
                .lunch(userEntity.getLunch())
                .dinner(userEntity.getDinner())
                .bedTime(userEntity.getBedTime())
                .build()
                ;
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
}
