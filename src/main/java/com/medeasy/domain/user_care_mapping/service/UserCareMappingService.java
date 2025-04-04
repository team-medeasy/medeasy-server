package com.medeasy.domain.user_care_mapping.service;

import com.medeasy.domain.user_care_mapping.db.UserCareMappingEntity;
import com.medeasy.domain.user_care_mapping.db.UserCareMappingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
