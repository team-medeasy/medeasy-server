package com.medeasy.domain.auth.controller;

import com.medeasy.common.api.Api;
import com.medeasy.domain.auth.business.AuthBusiness;
import com.medeasy.domain.auth.dto.LoginRequest;
import com.medeasy.domain.auth.dto.RefreshRequest;
import com.medeasy.domain.auth.dto.TokenDto;
import com.medeasy.domain.auth.dto.TokenResponse;
import com.medeasy.domain.user.dto.UserDto;
import com.medeasy.domain.user.dto.UserRegisterRequest;
import com.medeasy.domain.user.dto.UserResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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

    // 로그인 API
    @PostMapping("/login")
    public Api<TokenResponse> login(
            @Valid
            @RequestBody LoginRequest request
    ) {
        UserDto user=authBusiness.validateUser(request);
        TokenResponse tokenResponse=authBusiness.issueToken(user);

        return Api.OK(tokenResponse);
    }

    // 토큰 재발급
    @PostMapping("/refresh")
    public Api<TokenResponse> refresh(
            @Valid
            @RequestBody RefreshRequest request
    ) {
//        String refreshToken=request.getHeader("X-Refresh-Token");
        TokenResponse tokenResponse=authBusiness.recreateAccessToken(request.getRefreshToken());

        return Api.OK(tokenResponse);
    }
}
