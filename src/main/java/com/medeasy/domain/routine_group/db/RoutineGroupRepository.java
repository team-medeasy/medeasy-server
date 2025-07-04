package com.medeasy.domain.routine_group.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;

public interface RoutineGroupRepository extends JpaRepository<RoutineGroupEntity, Long> {

    @Query("SELECT rg FROM RoutineGroupEntity rg " +
            "JOIN rg.routines r " +
            "WHERE r.id=:routineId " +
            "AND rg.user.id=:userId")
    Optional<RoutineGroupEntity> findByRoutineIdAndUserId(Long routineId, Long userId);

    @Query("SELECT DISTINCT rg FROM RoutineGroupEntity rg " +
            "JOIN FETCH rg.routines r " +
            "JOIN FETCH r.userSchedule us " +
            "WHERE us.user.id = :userId AND rg.id = (" +
            "SELECT r2.routineGroup.id FROM RoutineEntity r2 WHERE r2.id = :routineId" +
            ") " +
            "ORDER BY r.takeDate ASC, us.takeTime ASC")
    Optional<RoutineGroupEntity> findRoutineGroupContainsRoutineIdByUserId(@Param("userId") Long userId, @Param("routineId") Long routineId);

    @Query("SELECT r.id FROM RoutineGroupEntity rg " +
            "JOIN rg.routines r " +
            "WHERE rg.user.id=:userId " +
            "AND rg.medicineId=:medicineId " +
            "ORDER BY rg.createdAt DESC " +
            "LIMIT 1")
    Optional<Long> findFirstRoutineIdByUserIdAndMedicineId(@Param("userId") Long userId, @Param("medicineId") String medicineId);

    List<RoutineGroupEntity> findByIdIn(List<Long> ids);
}
