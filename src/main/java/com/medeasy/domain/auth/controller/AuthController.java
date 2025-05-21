package com.medeasy.domain.auth.controller;

import com.medeasy.common.api.Api;
import com.medeasy.domain.auth.business.AuthBusiness;
import com.medeasy.domain.auth.dto.*;
import com.medeasy.domain.auth.service.KakaoService;
import com.medeasy.domain.user.dto.UserDto;
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
    private final KakaoService kakaoService;

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
    @Operation(summary = "로그인", description =
            """
            사용자 정보 확인 후 JWT 토큰 발급
            
            3/20 추가: 사용자 로그인시 FCM에서 발급받은 token 전달.
            
            값을 아예 넣지 않아도 null 처리 해놓은 상태
            """
    )
    public Api<TokenResponse> login(
            @Valid
            @RequestBody LoginRequest request
    ) {
        UserDto user=authBusiness.validateUser(request);
        TokenResponse tokenResponse=authBusiness.issueToken(user.getId());
        authBusiness.saveFcmToken(user.getId(), request.getFcmToken());
        log.info("사용자 로그인 완료: {}", user.getId());

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

        log.info("사용자 refresh token 재발급 완료");
        return Api.OK(tokenResponse);
    }

    @Operation(summary = "카카오 token을 통한 로그인", description = """
                        카카오 로그인 후 받은 access_token을 통해 서버 인증
                        
                        이전에 회원가입하지 않은 사용자인 경우 오류 발생 -> 서비스 회원가입 유도 
                        
                        추후 카카오로부터 비즈니스 승인을 받게 되면 자동 회원가입 기능 추가 
                    
                    """)
    @PostMapping("/kakao")
    public Api<Object> kakaoLogin(
        @RequestBody KaKaoLoginRequest request
    ) {
        Long userId=authBusiness.getUserIdKakao(request);
        TokenResponse tokenResponse=authBusiness.issueToken(userId);
        authBusiness.saveFcmToken(userId, request.getFcmToken());
        return Api.OK(tokenResponse);
    }

    @Operation(summary = "애플 로그인", description = """
                    
                    애플 로그인 후 받은 정보들을 가지고 애플 로그인한다.
                    
                    first_name과 last_name은 첫 로그인시에만 정보를 받을 수 있기 때문에, 이후 로그인에서는 null값을 넣는다.
                        
                    """)
    @PostMapping("/apple")
    public Api<Object> appleLogin(
            @RequestBody AppleLoginRequest request
    ) {
        Long userId=authBusiness.getUserIdApple(request);
        TokenResponse tokenResponse=authBusiness.issueToken(userId);
        authBusiness.saveFcmToken(userId, request.getFcmToken());
        return Api.OK(tokenResponse);
    }
}
