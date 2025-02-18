package com.medeasy.domain.user.controller;

import com.medeasy.common.api.Api;
import com.medeasy.domain.user.dto.UserRegisterRequest;
import com.medeasy.domain.user.dto.UserResponse;
import com.medeasy.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/open-api/user")
@RequiredArgsConstructor
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    @PostMapping("/register")
    public Api<UserResponse> register(
            @Valid
            @RequestBody(required = true) UserRegisterRequest userRegisterRequest
            ){
        UserResponse userResponse=userService.registerUser(userRegisterRequest);
        log.info(userResponse.getEmail());
        return Api.OK(userResponse);
    }

}
