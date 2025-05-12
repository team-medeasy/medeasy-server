package com.medeasy.domain.user_care_mapping.business;

import com.medeasy.common.annotation.Business;
import com.medeasy.common.error.ErrorCode;
import com.medeasy.common.exception.ApiException;
import com.medeasy.domain.routine.business.RoutineBusiness;
import com.medeasy.domain.routine.dto.RoutineGroupDto;
import com.medeasy.domain.user.db.UserEntity;
import com.medeasy.domain.user.service.UserService;
import com.medeasy.domain.user_care_mapping.service.UserCareMappingService;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Business
@RequiredArgsConstructor
public class UserCareBusiness {

    private final UserService userService;
    private final RoutineBusiness routineBusiness;
    private final UserCareMappingService userCareMappingService;

    public List<RoutineGroupDto> getCareReceiverRoutineListByDate(Long userId, Long careReceiverUserId, LocalDate startDate, LocalDate endDate) {
        UserEntity userEntity = userService.getUserWithCareReceivers(userId);
        List<UserEntity> receivers=userCareMappingService.getUserCareReceivers(userEntity);

        boolean isAuthorizedReceiver = receivers.stream()
                .anyMatch(receiver -> receiver.getId().equals(careReceiverUserId));

        if (!isAuthorizedReceiver) {
            throw new ApiException(ErrorCode.NOT_FOUND, "조회하려는 보호대상이 존재하지 않습니다.");
        }

        List<RoutineGroupDto> routineGroupDtos=routineBusiness.getRoutineListByDate(careReceiverUserId, startDate, endDate);
        return routineGroupDtos;
    }
}
