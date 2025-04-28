package com.medeasy.domain.interested_medicine.db;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InterestedMedicineRepository extends JpaRepository<InterestedMedicineEntity, Long> {
    Optional<InterestedMedicineEntity> findByUserIdAndMedicineId(Long userId, String medicineId);
}
