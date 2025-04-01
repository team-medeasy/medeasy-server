package com.medeasy.domain.user_care_mapping.converter;

import com.medeasy.common.annotation.Converter;
import com.medeasy.domain.user.db.UserEntity;
import com.medeasy.domain.user_care_mapping.db.UserCareMappingEntity;

@Converter
public class UserCareMappingConverter {

    public UserCareMappingEntity registerCareRelation(UserEntity careProvider, UserEntity careReceiver) {
        return UserCareMappingEntity.builder()
                .careReceiver(careReceiver)
                .careProvider(careProvider)
                .build()
                ;
    }
}
