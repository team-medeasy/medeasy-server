package com.medeasy.domain.medicine.db;

import com.medeasy.domain.search.db.SearchHistoryDocument;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface MedicineSearchCustomRepository {

    List<MedicineDocument> findMedicineBySearching(String itemName, List<String> colors, List<String> shape, Pageable pageable);

    MedicineDocument findByEdiCode(String ediCode);

    List<MedicineDocument> findMedicineByEdiCodeAndItemName(String ediCode, String itemName, Pageable pageable);

    List<MedicineDocument> findSimilarMedicines(String className, List<String> indications, Pageable pageable);

    List<MedicineDocument> findMedicinesByIds(List<String> ids);

    List<MedicineDocument> findMedicinesByMget(List<String> ids);

    void updateMedicineAudioUrl(String medicineId, String audioUrl);

    Optional<MedicineDocument> findFirstMedicineByName(String medicineName);
}
