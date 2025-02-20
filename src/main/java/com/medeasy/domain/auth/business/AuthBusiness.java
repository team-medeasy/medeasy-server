package com.medeasy.domain.auth.business;

import com.medeasy.common.annotation.Business;
import com.medeasy.common.error.TokenErrorCode;
import com.medeasy.common.error.UserErrorCode;
import com.medeasy.common.exception.ApiException;
import com.medeasy.domain.auth.dto.LoginRequest;
import com.medeasy.domain.auth.dto.TokenDto;
import com.medeasy.domain.auth.dto.TokenResponse;
import com.medeasy.domain.auth.util.TokenHelperIfs;
import com.medeasy.domain.user.db.UserEntity;
import com.medeasy.domain.user.dto.UserDto;
import com.medeasy.domain.user.dto.UserRegisterRequest;
import com.medeasy.domain.user.dto.UserResponse;
import com.medeasy.domain.user.service.UserConverter;
import com.medeasy.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.Map;

@Business
@RequiredArgsConstructor
public class AuthBusiness {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final UserConverter userConverter;
    private final TokenHelperIfs jwtTokenHelper; ;

    // 사용자 등록 비밀번호 해싱
    public UserResponse registerUser(UserRegisterRequest userRegisterRequest) {

        userRegisterRequest.setPassword(passwordEncoder.encode(userRegisterRequest.getPassword()));
        return userService.registerUser(userRegisterRequest);
    }

    // 사용자 비밀번호 검증
    public UserDto validateUser(LoginRequest request) {
        UserEntity user = userService.getUserByEmail(request.getEmail());
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw  new ApiException(UserErrorCode.USER_NOT_FOUNT);
        }
        return userConverter.toDto(user);
    }

    // 토큰 발급
    public TokenResponse issueToken(UserDto userDto) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userDto.getId());

        TokenDto acessToken = jwtTokenHelper.issueAcessToken(claims);
        TokenDto refreshToken = jwtTokenHelper.issueRefreshToken(claims);

        return TokenResponse.builder()
                .accessToken(acessToken.getToken())
                .accessTokenExpiredAt(acessToken.getExpiredAt())
                .refreshToken(refreshToken.getToken())
                .refreshTokenExpiredAt(refreshToken.getExpiredAt())
                .build()
                ;
    }

    public TokenResponse recreateAccessToken(String refreshToken) {
        TokenDto accessToken = jwtTokenHelper.recreateAccessToken(refreshToken);
        return TokenResponse.builder()
                .accessToken(accessToken.getToken())
                .accessTokenExpiredAt(accessToken.getExpiredAt())
                .build()
                ;
    }
}
