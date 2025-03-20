package com.medeasy.domain.auth.business;

import com.medeasy.common.annotation.Business;
import com.medeasy.common.error.UserErrorCode;
import com.medeasy.common.exception.ApiException;
import com.medeasy.domain.auth.dto.LoginRequest;
import com.medeasy.domain.auth.dto.TokenDto;
import com.medeasy.domain.auth.dto.TokenResponse;
import com.medeasy.domain.auth.util.TokenHelperIfs;
import com.medeasy.domain.user.db.UserEntity;
import com.medeasy.domain.user.dto.UserDto;
import com.medeasy.domain.auth.dto.UserRegisterRequest;
import com.medeasy.domain.user.dto.UserResponse;
import com.medeasy.domain.user.service.UserConverter;
import com.medeasy.domain.user.service.UserService;
import com.medeasy.domain.user_schedule.business.UserScheduleBusiness;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Business
public class AuthBusiness {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final UserConverter userConverter;
    private final TokenHelperIfs jwtTokenHelper; ;
    private final UserScheduleBusiness userScheduleBusiness;
    private final StringRedisTemplate redisTemplateForJwt;

    public AuthBusiness(
            UserService userService,
            PasswordEncoder passwordEncoder,
            UserConverter userConverter,
            TokenHelperIfs jwtTokenHelper,
            UserScheduleBusiness userScheduleBusiness,
            @Qualifier("redisTemplateForJwt") StringRedisTemplate redisTemplateForJwt
    ) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.userConverter = userConverter;
        this.jwtTokenHelper = jwtTokenHelper;
        this.userScheduleBusiness = userScheduleBusiness;
        this.redisTemplateForJwt = redisTemplateForJwt;
    }


    /**
     * 회원가입 메서드
     * 1. 사용자 이메일 중복 검사 및 등록
     * 2. 사용자 기본 루틴 스케줄 추가
     * */
    @Transactional
    public UserResponse registerUser(UserRegisterRequest userRegisterRequest) {

        userRegisterRequest.setPassword(passwordEncoder.encode(userRegisterRequest.getPassword()));
        UserEntity userEntity =userConverter.toEntity(userRegisterRequest);
        UserEntity newUserEntity=userService.registerUser(userEntity);
        userScheduleBusiness.registerUserDefaultSchedule(userEntity);

        return userConverter.toResponse(newUserEntity);
    }

    // 사용자 비밀번호 검증
    public UserDto validateUser(LoginRequest request) {
        UserEntity user = userService.getUserByEmail(request.getEmail());
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ApiException(UserErrorCode.USER_NOT_FOUNT);
        }
        return userConverter.toDto(user);
    }

    // 토큰 발급
    public TokenResponse issueToken(UserDto userDto) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userDto.getId());

        TokenDto accessToken = jwtTokenHelper.issueAcessToken(claims);
        TokenDto refreshToken = jwtTokenHelper.issueRefreshToken(claims);

        return TokenResponse.builder()
                .accessToken(accessToken.getToken())
                .accessTokenExpiredAt(accessToken.getExpiredAt())
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

    public void saveFcmToken(Long userId, String fcmToken) {
        String fcmKey="fcm_tokens:"+userId;

        if(fcmToken == null){
            fcmToken="";
        }

        redisTemplateForJwt.opsForSet().add(fcmKey, fcmToken);
    }
}
