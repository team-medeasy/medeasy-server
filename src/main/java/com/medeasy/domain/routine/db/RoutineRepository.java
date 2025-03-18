package com.medeasy.domain.routine.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RoutineRepository extends JpaRepository<RoutineEntity, Long> {
    @Query("SELECT r FROM RoutineEntity r " +
            "JOIN FETCH r.userSchedule us " +
            "LEFT JOIN FETCH r.routineMedicines rm " +
            "WHERE r.user.id = :userId " +
            "AND r.takeDate BETWEEN :startDate AND :endDate " +
            "ORDER BY r.takeDate ASC, us.takeTime ASC, rm.id ASC")
    List<RoutineEntity> findGroupedRoutinesByDate(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query(value = "SELECT r from RoutineEntity r " +
            "where r.user.id=:userId " +
            "and r.userSchedule.id in :userScheduleIds " +
            "and r.takeDate in :takeDates")
    List<RoutineEntity> findAllByByUserIdUserScheduleIdsAndTakeDates(
            @Param("userId") Long userId,
            @Param("userScheduleIds") List<Long> userScheduleIds,
            @Param("takeDates") List<LocalDate> takeDates
    );
}
