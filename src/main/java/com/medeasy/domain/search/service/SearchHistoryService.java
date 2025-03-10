package com.medeasy.domain.search.service;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOptionsBuilders;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import com.medeasy.domain.search.db.SearchHistoryDocument;
import com.medeasy.domain.search.db.SearchHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchHistoryService {

    private final SearchHistoryRepository searchHistoryRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    public void saveSearchKeyword(String userId, String keyword) {
        SearchHistoryDocument searchHistoryDocument = SearchHistoryDocument.builder()
                .userId(userId)
                .keyword(keyword)
                .searchTime(Instant.now())
                .build()
                ;

        searchHistoryRepository.save(searchHistoryDocument);
    }

    public List<SearchHistoryDocument> getUserSearchHistories(Long userId, int size) {
        Query searchQuery = QueryBuilders.term(termQueryBuilder -> {
            return termQueryBuilder.field("userId").value(userId);
        });

        SortOptions sortOptions=SortOptionsBuilders.field(fieldSortBuilder -> {
            return fieldSortBuilder.field("searchTime")
                    .order(SortOrder.Desc);
        });

        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(searchQuery)
                .withSort(sortOptions)
                .withPageable(PageRequest.of(0, size))
                .build()
                ;
        SearchHits<SearchHistoryDocument> searchHits=elasticsearchOperations.search(nativeQuery, SearchHistoryDocument.class);

        return searchHits.getSearchHits()
                .stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }
}
