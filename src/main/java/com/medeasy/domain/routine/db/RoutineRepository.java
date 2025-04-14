package com.medeasy.domain.routine.db;

import com.medeasy.domain.routine.dto.RoutineGroupDateRangeDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.swing.text.html.Option;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RoutineRepository extends JpaRepository<RoutineEntity, Long> {
    Optional<RoutineEntity> findByUserScheduleIdAndTakeDate(Long userScheduleId, LocalDate takeDate);

    @Query("SELECT r FROM RoutineEntity r " +
            "JOIN FETCH r.userSchedule us " +
            "WHERE r.user.id = :userId " +
            "AND r.takeDate BETWEEN :startDate AND :endDate " +
            "ORDER BY r.takeDate ASC, us.takeTime ASC")
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

    @Query("SELECT new com.medeasy.domain.routine.dto.RoutineGroupDateRangeDto(rg.id, MIN(r.takeDate), MAX(r.takeDate)) " +
            "FROM RoutineEntity r JOIN r.routineGroup rg " +
            "WHERE r.user.id = :userId " +
            "GROUP BY rg.id")
    List<RoutineGroupDateRangeDto> findStartAndEndDateRangeByGroup(Long userId);

    void deleteByUserIdAndId(Long userId, Long id);

    @Query("SELECT DISTINCT r.medicineId " +
            "FROM RoutineEntity r " +
            "WHERE r.user.id = :userId ")
    List<String> findDistinctMeidicneIdByUserId(Long userId);

    Optional<RoutineEntity> findByUserIdAndId(Long userId, Long id);
}
