package com.medeasy.domain.routine.controller;

import com.medeasy.common.api.Api;
import com.medeasy.domain.routine.dto.RoutineResponse;
import com.medeasy.domain.routine.service.RoutineService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/routine")
@RequiredArgsConstructor
public class RoutineController {

    private final RoutineService routineService;

    @PostMapping("")
    public Api<RoutineResponse> registerRoutine() {


        return null;
    }
}
