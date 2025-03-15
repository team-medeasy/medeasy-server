package com.medeasy.domain.auth.controller;

import com.medeasy.common.api.Api;
import com.medeasy.domain.auth.business.AuthBusiness;
import com.medeasy.domain.auth.dto.LoginRequest;
import com.medeasy.domain.auth.dto.RefreshRequest;
import com.medeasy.domain.auth.dto.TokenResponse;
import com.medeasy.domain.user.dto.UserDto;
import com.medeasy.domain.auth.dto.UserRegisterRequest;
import com.medeasy.domain.user.dto.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
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
    @Operation(summary = "회원가입", description =
            """
            회원가입 API
            """
    )
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
    @Operation(summary = "로그인", description = "사용자 정보 확인 후 JWT 토큰 발급")
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
    @Operation(summary = "JWT 토큰 재발급", description = "refresh_token을 통해 access_token 재발급")
    public Api<TokenResponse> refresh(
            @Valid
            @RequestBody RefreshRequest request
    ) {
        TokenResponse tokenResponse=authBusiness.recreateAccessToken(request.getRefreshToken());

        return Api.OK(tokenResponse);
    }
}
