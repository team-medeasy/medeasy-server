package com.medeasy.domain.user_care_mapping.service;

import com.medeasy.domain.user.db.UserEntity;
import com.medeasy.domain.user_care_mapping.db.UserCareMappingEntity;
import com.medeasy.domain.user_care_mapping.db.UserCareMappingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserCareMappingService {

    private final UserCareMappingRepository userCareMappingRepository;

    public UserCareMappingEntity save(UserCareMappingEntity userCareMappingEntity) {
        return userCareMappingRepository.save(userCareMappingEntity);
    }

    public List<UserCareMappingEntity> findAllCareReceivers(Long userId){
        return userCareMappingRepository.findAllByCareProviderIdWithFetchJoin(userId);
    }

    public void deleteCareReceiver(Long userId, Long receiverId) {
        userCareMappingRepository.deleteByCareProviderIdAndCareReceiverId(userId, receiverId);
    }

    /**
     * 사용자의 보호 대상들을 조회하는 메서드
     * */
    public List<UserEntity> getUserCareReceivers(UserEntity userEntity) {
        return Optional.ofNullable(userEntity.getCareReceivers())
                .orElse(Collections.emptyList())
                .stream()
                .map(UserCareMappingEntity::getCareReceiver)
                .toList();
    }
}
