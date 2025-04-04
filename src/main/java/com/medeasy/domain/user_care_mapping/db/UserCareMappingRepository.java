package com.medeasy.domain.user_care_mapping.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserCareMappingRepository extends JpaRepository<UserCareMappingEntity, Long> {

    @Query("SELECT uc FROM UserCareMappingEntity uc " +
            "JOIN FETCH uc.careReceiver " +
            "WHERE uc.careProvider.id=:userId")
    List<UserCareMappingEntity> findAllByCareProviderIdWithFetchJoin(@Param("userId") Long userId);

    void deleteByCareProviderIdAndCareReceiverId(Long userId, Long careProviderId);
}
