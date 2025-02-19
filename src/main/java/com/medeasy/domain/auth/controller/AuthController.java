package com.medeasy.domain.user.controller;

import com.medeasy.common.api.Api;
import com.medeasy.domain.user.business.AuthBusiness;
import com.medeasy.domain.user.dto.UserRegisterRequest;
import com.medeasy.domain.user.dto.UserResponse;
import com.medeasy.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/open-api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthBusiness authBusiness;

    @PostMapping("/sign_up")
    public Api<UserResponse> register(
            @Valid
            @RequestBody(required = true) UserRegisterRequest userRegisterRequest
            ){
        UserResponse userResponse=authBusiness.registerUser(userRegisterRequest);
        log.info("{}가입 완료", userResponse.getEmail());
        return Api.OK(userResponse);
    }

}
