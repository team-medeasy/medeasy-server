package com.medeasy.domain.user_schedule.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserScheduleRepository extends JpaRepository<UserScheduleEntity, Long> {
    List<UserScheduleEntity> findAllByIdInOrderByTakeTimeAsc(List<Long> ids);

    @Query("SELECT us FROM UserScheduleEntity us " +
            "LEFT JOIN FETCH us.routine " +
            "WHERE us.id = :id")
    Optional<UserScheduleEntity> findByIdByFetchJoin(@Param("id") Long id);
}
