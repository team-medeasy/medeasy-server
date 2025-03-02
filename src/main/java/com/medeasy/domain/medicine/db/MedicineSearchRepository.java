package com.medeasy.domain.medicine.db;

import jakarta.json.JsonArray;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface MedicineSearchRepository extends ElasticsearchRepository<MedicineDocument, String> {

    // containing이 prefix 방식으로 저장하는 듯 -> 아님 match_phrase에 가까움
    List<MedicineDocument> findByItemNameContaining(String itemName, Pageable pageable);

    // 약 이름 검색 + 여러 개의 색상 검색 가능
    // TODO 하나의 필드로 둔 color -> color1, color2로 수정
    @Query("""
    {
      "bool": {
        "must": [
          { "match": { "itemName": "?0" } }
        ],
        "should": [
          { "terms": { "color": #{#colors}} }
        ],
        "minimum_should_match": 1
      }
    }
    """)
    List<MedicineDocument> findByItemNameAndColors(String itemName, List<String> colors, Pageable pageable);
}
