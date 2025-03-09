package com.medeasy.domain.medicine.db;

import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface MedicineSearchRepository extends ElasticsearchRepository<MedicineDocument, String> {

    // containing이 prefix 방식으로 저장하는 듯 -> 아님 match_phrase에 가까움
    List<MedicineDocument> findByItemNameContaining(String itemName, Pageable pageable);

}
