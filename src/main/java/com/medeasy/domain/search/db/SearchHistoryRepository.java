package com.medeasy.domain.search.db;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface SearchHistoryRepository extends ElasticsearchRepository<SearchHistoryDocument, String> {

}
