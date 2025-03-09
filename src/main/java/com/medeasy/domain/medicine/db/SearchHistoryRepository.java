package com.medeasy.domain.medicine.db;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface SearchHistoryRepository extends ElasticsearchRepository<SearchHistoryDocument, String> {
}
