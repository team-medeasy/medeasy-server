package com.medeasy.domain.search.db;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface UserSearchHistoryRepository extends ElasticsearchRepository<UserSearchHistoryDocument, String> {
    void deleteAllByUserId(String userId);
}
