package com.medeasy.domain.auth.business;

import com.medeasy.common.annotation.Business;
import com.medeasy.common.error.TokenErrorCode;
import com.medeasy.common.error.UserErrorCode;
import com.medeasy.common.exception.ApiException;
import com.medeasy.domain.auth.dto.LoginRequest;
import com.medeasy.domain.auth.dto.TokenDto;
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

    public TokenDto issueToken(UserDto userDto) {
        // JWT 클레임에 필요한 정보 추가
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userDto.getId());

        TokenDto tokenDto = jwtTokenHelper.issueAcessToken(claims);

        return tokenDto;
    }
}
