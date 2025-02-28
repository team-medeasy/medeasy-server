package com.medeasy.domain.user.controller;

import com.medeasy.common.annotation.UserSession;
import com.medeasy.common.api.Api;
import com.medeasy.domain.user.dto.RoutineScheduleRequest;
import com.medeasy.domain.user.business.UserBusiness;
import com.medeasy.domain.user.dto.UserScheduleResponse;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserBusiness userBusiness;

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
}
