package com.medeasy.domain.routine.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface RoutineRepository extends JpaRepository<RoutineEntity, Long> {
    List<RoutineEntity> findAllByTakeDateAndUserIdOrderByTakeTimeAsc(LocalDate takeDate, Long userId);

    /**
     * 사용자의 특정 날짜 루틴들을
     * take_time으로 그룹화하여
     * json 배열로 리턴
     *
     * response:
     * [{
     *     "take_time" : "08:00",
     *     "routines": [
     *          {
     *             "routine_id" : 1,
     *             "medicine_id" : 101,
     *             "nickname" : 비타민
     *          }
     *      ]
     *      "take_time" : "12:00",
     *      "routines" : [
     *          {
     *              "routine_id" : 2,
     *              "medicine_id" : 102,
     *              "nickname" : 비타민 C
     *          }
     *      ]
     * }]
     * */
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

    /**
     * 사용자가 복용 중인 약 개수 반환
     * */
    @Query("SELECT COUNT(DISTINCT r.medicine) " +
            "FROM RoutineEntity r " +
            "WHERE r.user.id = :user_id " +
            "AND r.isTaken = false")
    int countDistinctMedicineByUserIdAndIsTakenIsFalse(@Param("user_id") Long userId);

    @Query("SELECT DISTINCT r.medicine.id " +
            "FROM RoutineEntity r " +
            "WHERE r.user.id = :user_id " +
            "AND r.isTaken = false")
    List<Long> findDistinctMedicineIdByUserIdAndIsTakenIsFalse(@Param("user_id") Long userId);
}
