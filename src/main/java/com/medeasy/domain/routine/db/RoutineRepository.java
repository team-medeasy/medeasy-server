package com.medeasy.domain.routine.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RoutineRepository extends JpaRepository<RoutineEntity, Long> {
    List<RoutineEntity> findAllByTakeDateAndUserIdOrderByTakeTimeAsc(LocalDate takeDate, Long userId);

    // json 객체의 속성 이름 : 데이터베이스 컬럼 값
    @Query(value = "SELECT take_time, " +
            "       ARRAY_TO_JSON(ARRAY_AGG(jsonb_build_object(" +
            "           'routine_id', id, " +
            "           'medicine_id', medicine_id, " +
            "           'nickname', nickname, " +
            "           'take_time', take_time, " +
            "           'type', type, " +
            "           'is_taken', is_taken, " +
            "           'take_date', take_date" +
            "       ) ORDER BY take_time)) AS routines " +
            "FROM routine " +
            "WHERE take_date = :take_date " +
            "  AND user_id = :user_id " +
            "GROUP BY take_time " +
            "ORDER BY take_time", nativeQuery = true)
    List<Object[]> findRoutinesByTakeDateAndUserId(
            @Param("take_date")LocalDate takeDate,
            @Param("user_id") Long userId
    );

}
