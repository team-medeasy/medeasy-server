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
            "JOIN FETCH r.routineGroup rg " +
            "WHERE rg.user.id = :userId " +
            "AND r.takeDate BETWEEN :startDate AND :endDate " +
            "ORDER BY r.takeDate ASC, us.takeTime ASC")
    List<RoutineEntity> findGroupedRoutinesByDate(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    void deleteById(Long id);

    @Query("SELECT DISTINCT rg.medicineId " +
            "FROM RoutineGroupEntity rg " +
            "JOIN rg.routines r " +
            "WHERE rg.user.id = :userId ")
    List<String> findDistinctMeidicneIdByUserId(Long userId);

    @Query("SELECT r FROM RoutineEntity r " +
            "JOIN FETCH r.routineGroup rg " +
            "WHERE rg.user.id=:userId " +
            "AND r.id= :id ")
    Optional<RoutineEntity> findRoutineByUserIdAndId(Long userId, Long id);

    @Query("SELECT r FROM RoutineEntity r " +
            "JOIN FETCH r.userSchedule us " +
            "WHERE us.user.id = :user_id " +
            "AND us.id = :user_schedule_id")
    List<RoutineEntity> findRoutinesOnScheduleId(
            @Param("user_id") Long userId,
            @Param("user_schedule_id") Long userScheduleId
    );

    @Query("SELECT r FROM RoutineEntity r " +
            "JOIN FETCH r.userSchedule us " +
            "JOIN FETCH r.routineGroup rg " +
            "WHERE us.user.id = :user_id " +
            "AND us.id = :user_schedule_id " +
            "AND r.isTaken = false ")
    List<RoutineEntity> findNotTakenRoutinesOnScheduleIdWithRoutineGroup(
            @Param("user_id") Long userId,
            @Param("user_schedule_id") Long userScheduleId
    );
}
