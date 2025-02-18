package com.medeasy.domain.user.service;

import com.medeasy.common.annotation.Converter;
import com.medeasy.domain.user.db.UserEntity;
import com.medeasy.domain.user.dto.UserRegisterRequest;
import com.medeasy.domain.user.dto.UserResponse;

@Converter
public class UserConverter {

    public UserEntity toEntity(UserRegisterRequest userRegisterRequest) {
        return UserEntity.builder()
                .email(userRegisterRequest.getEmail())
                .name(userRegisterRequest.getName())
                .password(userRegisterRequest.getPassword())
                .birthday(userRegisterRequest.getBirthday())
                .gender(userRegisterRequest.getGender())
                .nok(null)
                .build()
                ;
    }

    public UserResponse toResponse(UserEntity userEntity) {
        return UserResponse.builder()
                .id(userEntity.getId())
                .email(userEntity.getEmail())
                .name(userEntity.getName())
                .gender(userEntity.getGender())
                .registeredAt(userEntity.getRegisteredAt())
                .loginedAt(userEntity.getLoginedAt())
                .birthday(userEntity.getBirthday())
                .build()
                ;
    }
}
