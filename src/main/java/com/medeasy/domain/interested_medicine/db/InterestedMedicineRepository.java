package com.medeasy.domain.interested_medicine.db;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InterestedMedicineRepository extends JpaRepository<InterestedMedicineEntity, Long> {
    Optional<InterestedMedicineEntity> findByUserIdAndMedicineId(Long userId, String medicineId);

    List<InterestedMedicineEntity> findByUserId(Long userId, Pageable pageable);
}
