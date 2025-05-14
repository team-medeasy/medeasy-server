package com.medeasy.domain.medicine.db;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DrugContraindicationsRepository extends ElasticsearchRepository<DrugContraindicationsDocument, String> {
    Optional<DrugContraindicationsDocument> findByItemSeq(String itemSeq);

    // 임신 금기 사항에 특정 단어가 포함된 문서 검색
    List<DrugContraindicationsDocument> findByPregnancyContraindicationContaining(String keyword);
}
