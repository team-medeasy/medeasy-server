package com.medeasy.domain.search.service;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOptionsBuilders;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregate;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.aggregations.AggregationBuilders;
import co.elastic.clients.elasticsearch._types.aggregations.MaxAggregate;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import com.medeasy.common.error.ErrorCode;
import com.medeasy.common.exception.ApiException;
import com.medeasy.domain.search.db.SearchHistoryDocument;
import com.medeasy.domain.search.db.SearchHistoryRepository;
import com.medeasy.domain.search.db.SearchPopularDocument;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregation;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregations;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
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

    public void deleteSearchHistory(Long userId, String searchHistoryId) {
        SearchHistoryDocument searchHistoryDocument = searchHistoryRepository.findById(searchHistoryId)
                .orElseThrow(() -> new ApiException(ErrorCode.BAD_REQEUST, "해당하는 검색 기록이 없습니다."));

        searchHistoryRepository.delete(searchHistoryDocument);
    }

    /**
     * 랭킹 조회 메서드
     * */
    public List<SearchPopularDocument> getLatestPopularUpdatedTime() {
        Aggregation latestSearchTimeAggregation = AggregationBuilders.max(maxAggregationBuilder -> {
            return maxAggregationBuilder.field("updatedAt");
        });

        NativeQuery nativeQuery = NativeQuery.builder()
                .withAggregation("latest_updatedAt", latestSearchTimeAggregation)
                .withPageable(PageRequest.of(0, 100))
                .build()
                ;
        SearchHits<SearchPopularDocument> searchHits=elasticsearchOperations.search(nativeQuery, SearchPopularDocument.class);

        ElasticsearchAggregations aggregations = (ElasticsearchAggregations) searchHits.getAggregations();
        ElasticsearchAggregation aggregation = aggregations.aggregationsAsMap().get("latest_updatedAt");

        String latestUpdatedAtAsString = aggregation.aggregation().getAggregate().max().valueAsString();

        Instant instant = Instant.parse(latestUpdatedAtAsString);

        log.info("랭킹 조회할 최신 시간대: {}", latestUpdatedAtAsString);
        return null;
    }

    public List<SearchPopularDocument> getSearchPopularByDate(Instant updatedAt) {
        Query searchQuery = QueryBuilders.term(termQueryBuilder->{
            return termQueryBuilder.field("updatedAt").value(FieldValue.of(updatedAt));
        });
    }
}
