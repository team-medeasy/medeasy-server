package com.medeasy.domain.medicine.db;

import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface MedicineSearchRepository extends ElasticsearchRepository<MedicineDocument, String> {
    List<MedicineDocument> findByItemNameContaining(String itemName, Pageable pageable);
}
