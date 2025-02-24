package com.medeasy.domain.user.service;

import com.medeasy.common.error.ErrorCode;
import com.medeasy.common.error.UserErrorCode;
import com.medeasy.common.exception.ApiException;
import com.medeasy.domain.user.db.UserEntity;
import com.medeasy.domain.user.db.UserRepository;
import com.medeasy.domain.auth.dto.UserRegisterRequest;
import com.medeasy.domain.user.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserConverter userConverter;

    public UserResponse registerUser(UserRegisterRequest userRegisterRequest) {

        try{
            UserEntity userEntity =userConverter.toEntity(userRegisterRequest);
            UserEntity newUserEntity = userRepository.save(userEntity);

            UserResponse userResponse=userConverter.toResponse(newUserEntity);

            return userResponse;
        }catch (DataIntegrityViolationException e){
            throw new ApiException(ErrorCode.BAD_REQEUST, "중복된 이메일입니다.");
        }
    }

    public UserEntity getUserByEmail(String email) {
        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(()-> new ApiException(UserErrorCode.USER_NOT_FOUNT));

        return userEntity;
    }

    public UserEntity getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(()-> new ApiException(UserErrorCode.USER_NOT_FOUNT));
    }
}
