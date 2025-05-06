package com.medeasy.domain.user.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);

    @Query("SELECT u from UserEntity u " +
            "LEFT JOIN FETCH u.userSchedules " +
            "WHERE u.id = :userId ")
    Optional<UserEntity> findByIdToFetchJoin(
            @Param("userId") Long userId
    );

    @Query("SELECT u FROM UserEntity u " +
            "LEFT JOIN FETCH u.careReceivers crm " +
            "LEFT JOIN FETCH crm.careReceiver receiver " +
            "WHERE u.id=:userId ")
    Optional<UserEntity> findByIdWithUserCareReceiver(
            @Param("userId") Long userId
    );
}
