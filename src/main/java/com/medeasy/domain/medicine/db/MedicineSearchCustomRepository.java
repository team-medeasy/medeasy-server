package com.medeasy.domain.medicine.db;

import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MedicineSearchCustomRepository {

    List<MedicineDocument> findByItemNameAndColor(String itemName, String color, Pageable pageable);

}
