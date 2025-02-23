package com.medeasy.domain.routine.controller;

import com.medeasy.common.annotation.UserSession;
import com.medeasy.common.api.Api;
import com.medeasy.domain.routine.business.RoutineBusiness;
import com.medeasy.domain.routine.dto.RoutineRegisterRequest;
import com.medeasy.domain.routine.dto.RoutineResponse;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/routine")
@RequiredArgsConstructor
public class RoutineController {

    private final RoutineBusiness routineBusiness;

    @PostMapping("")
    public Api<RoutineResponse> registerRoutine(
            @Parameter(hidden = true) @UserSession Long userId,
            @Valid
            @RequestBody RoutineRegisterRequest routineRegisterRequest
    ) {
        routineBusiness.registerRoutine(userId, routineRegisterRequest);

        return null;
    }
}
