package com.medeasy.domain.routine_medicine.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RoutineMedicineRepository extends JpaRepository<RoutineMedicineEntity, Long> {

    @Query("SELECT DISTINCT rm.medicineId FROM RoutineMedicineEntity rm " +
            "JOIN rm.routine r " +
            "WHERE r.user.id = :userId ")
    List<Long> findDistinctRoutineMedicinesByUserId(@Param("userId") Long userId);
}
