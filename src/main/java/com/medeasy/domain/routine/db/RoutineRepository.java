package com.medeasy.domain.routine.db;

import com.medeasy.domain.schedular.dto.RoutineNativeQueryDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface RoutineRepository extends JpaRepository<RoutineEntity, Long> {
    Optional<RoutineEntity> findByUserScheduleIdAndTakeDate(Long userScheduleId, LocalDate takeDate);

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

    @Query("SELECT r FROM RoutineEntity r " +
            "JOIN FETCH r.userSchedule us " +
            "JOIN FETCH r.routineMedicines rm " +
            "WHERE r.takeDate = :date " +
            "AND us.takeTime BETWEEN :startTime AND :endTime")
    List<RoutineEntity> findAllByTakeDateAndTakeTimeBetweenWithMedicine(
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime);


/*    @Query(value = "SELECT r.id AS routineId, us.name AS scheduleName, r.take_date AS takeDate, " +
            "rm.medicine_id AS medicineId, rm.nickname AS medicineNickname, rm.dose AS dose, " +
            "us.user_id AS userId, us.take_time AS takeTime FROM " +
            "(SELECT * FROM routine WHERE take_date = :date) r " +
            "JOIN user_schedule us ON r.user_schedule_id = us.id " +
            "JOIN routine_medicine rm ON r.id = rm.routine_id " +
            "WHERE us.take_time BETWEEN :startTime AND :endTime",
            nativeQuery = true)
    List<RoutineNativeQueryDto> findAllByTakeDateAndTakeTimeBetweenWithMedicineNative(
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime);*/
}
