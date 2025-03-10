package com.medeasy.domain.search.service;

import com.medeasy.domain.medicine.db.SearchHistoryDocument;
import com.medeasy.domain.medicine.db.SearchHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class SearchHistoryService {

    private final SearchHistoryRepository searchHistoryRepository;

    public void saveSearchKeyword(String userId, String keyword) {
        SearchHistoryDocument searchHistoryDocument = SearchHistoryDocument.builder()
                .userId(userId)
                .keyword(keyword)
                .searchTime(Instant.now())
                .build()
                ;

        searchHistoryRepository.save(searchHistoryDocument);
    }
}
