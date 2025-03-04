package com.medeasy.domain.medicine.db;

import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MedicineSearchCustomRepository {

    List<MedicineDocument> findMedicineBySearching(String itemName, List<String> colors, List<String> shape, Pageable pageable);

}
