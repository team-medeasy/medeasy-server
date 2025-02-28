package com.medeasy.domain.user.business;

import com.medeasy.common.annotation.Business;
import com.medeasy.domain.user.dto.RoutineScheduleRequest;
import com.medeasy.domain.user.db.UserEntity;
import com.medeasy.domain.user.dto.UserScheduleResponse;
import com.medeasy.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Business
@RequiredArgsConstructor
public class UserBusiness {

    private final UserService userService;

    /**
     * request의 null이 아닌 수정사항만 사용자의 정보에서 업데잋트
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
}
