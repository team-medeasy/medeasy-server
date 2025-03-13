package com.medeasy.domain.search.service;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOptionsBuilders;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.aggregations.AggregationBuilders;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import com.medeasy.common.error.ErrorCode;
import com.medeasy.common.error.SearchHistoryError;
import com.medeasy.common.exception.ApiException;
import com.medeasy.domain.search.db.SearchHistoryDocument;
import com.medeasy.domain.search.db.SearchHistoryRepository;
import com.medeasy.domain.search.db.SearchPopularDocument;
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

    /**
     * 사용자의 검색 기록 조회 메서드
     * */
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
        try {
            searchHistoryRepository.delete(searchHistoryDocument);
        }catch (Exception e) {
            throw new ApiException(SearchHistoryError.SERVER_ERROR, "사용자 검색 기록 삭제 중 오류 발생");
        }
    }


    public void deleteAllUserSearchHistory(Long userId) {
        try {
            searchHistoryRepository.deleteAllByUserId(userId.toString());
        }catch (Exception e){
            throw new ApiException(SearchHistoryError.SERVER_ERROR, "사용자 검색 기록 삭제 중 오류 발생");
        }
    }
    /**
     * 인기 검색어 가장 마지막 업데이트 시간 반환 메서드
     * */
    public String getLatestPopularUpdatedTime() {
        Aggregation latestSearchTimeAggregation = AggregationBuilders.max(maxAggregationBuilder -> {
            return maxAggregationBuilder.field("updatedAt");
        });

        NativeQuery nativeQuery = NativeQuery.builder()
                .withAggregation("latest_updatedAt", latestSearchTimeAggregation)
                .withPageable(PageRequest.of(0, 100))
                .build()
                ;

        try {
            SearchHits<SearchPopularDocument> searchHits=elasticsearchOperations.search(nativeQuery, SearchPopularDocument.class);

            ElasticsearchAggregations aggregations = (ElasticsearchAggregations) searchHits.getAggregations();
            ElasticsearchAggregation aggregation = aggregations.aggregationsAsMap().get("latest_updatedAt");

            String latestUpdatedAtAsString = aggregation.aggregation().getAggregate().max().valueAsString();

            log.info("랭킹 조회할 최신 시간대: {}", latestUpdatedAtAsString);
            return latestUpdatedAtAsString;
        }catch (Exception e){
            throw new ApiException(SearchHistoryError.SERVER_ERROR, "인기 검색어 마지막 업데이트 시간 조회 오류");
        }
    }

    /**
     * 문자열 UTC 날짜, 시간을 가지고 인기 검색어 리스트 문서를 반환하는 메서드
     * */
    public List<SearchPopularDocument> getSearchPopularByDate(String updatedAt) {
        Query searchQuery = QueryBuilders.term(termQueryBuilder->{
            return termQueryBuilder.field("updatedAt").value(updatedAt);
        });

        SortOptions sortOptions = SortOptionsBuilders.field(fieldSortBuilder -> {
            return fieldSortBuilder.field("rank").order(SortOrder.Asc);
        });

        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(searchQuery)
                .withSort(sortOptions)
                .withPageable(PageRequest.of(0, 10))
                .build()
                ;

        try {
            SearchHits<SearchPopularDocument> searchHits = elasticsearchOperations.search(nativeQuery, SearchPopularDocument.class);

            return searchHits.getSearchHits()
                    .stream()
                    .map(SearchHit::getContent)
                    .collect(Collectors.toList());
        }catch (Exception e){
            throw new ApiException(SearchHistoryError.SERVER_ERROR, "인기 검색어 조회 중 오류 발생");
        }
    }

}
