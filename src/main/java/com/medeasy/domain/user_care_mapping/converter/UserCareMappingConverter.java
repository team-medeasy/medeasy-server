package com.medeasy.domain.user_care_mapping.converter;

import com.medeasy.common.annotation.Converter;
import com.medeasy.domain.user.db.UserEntity;
import com.medeasy.domain.user_care_mapping.db.UserCareMappingEntity;
import com.medeasy.domain.user_care_mapping.dto.CareReceiverResponse;

import java.time.LocalDateTime;

@Converter
public class UserCareMappingConverter {

    public UserCareMappingEntity registerCareRelation(UserEntity careProvider, UserEntity careReceiver) {
        return UserCareMappingEntity.builder()
                .careReceiver(careReceiver)
                .careProvider(careProvider)
                .registeredAt(LocalDateTime.now())
                .build()
                ;
    }

    public CareReceiverResponse toCareReceiverResponse(UserCareMappingEntity entity) {
        return CareReceiverResponse.builder()
                .receiverId(entity.getCareReceiver().getId())
                .receiverName(entity.getCareReceiver().getName())
                .build()
                ;
    }
}
