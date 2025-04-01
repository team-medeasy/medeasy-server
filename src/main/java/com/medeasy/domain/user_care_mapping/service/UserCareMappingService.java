package com.medeasy.domain.user_care_mapping.service;

import com.medeasy.domain.user_care_mapping.db.UserCareMappingEntity;
import com.medeasy.domain.user_care_mapping.db.UserCareMappingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserCareMappingService {

    private final UserCareMappingRepository userCareMappingRepository;

    public UserCareMappingEntity save(UserCareMappingEntity userCareMappingEntity) {
        return userCareMappingRepository.save(userCareMappingEntity);
    }
}
