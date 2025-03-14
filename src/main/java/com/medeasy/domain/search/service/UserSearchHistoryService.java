package com.medeasy.domain.search.service;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOptionsBuilders;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import com.medeasy.common.error.ErrorCode;
import com.medeasy.common.error.SearchHistoryError;
import com.medeasy.common.exception.ApiException;
import com.medeasy.domain.search.db.UserSearchHistoryDocument;
import com.medeasy.domain.search.db.UserSearchHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
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
public class UserSearchHistoryService {

    private final UserSearchHistoryRepository userSearchHistoryRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    public void saveSearchKeyword(String userId, String keyword) {
        UserSearchHistoryDocument userSearchHistoryDocument = UserSearchHistoryDocument.builder()
                .userId(userId)
                .keyword(keyword)
                .searchTime(Instant.now())
                .build();

        userSearchHistoryRepository.save(userSearchHistoryDocument);
    }

    /**
     * 사용자의 검색 기록 조회 메서드
     * */
    public List<UserSearchHistoryDocument> getUserSearchHistories(Long userId, int size) {
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
        SearchHits<UserSearchHistoryDocument> searchHits=elasticsearchOperations.search(nativeQuery, UserSearchHistoryDocument.class);

        return searchHits.getSearchHits()
                .stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }

    public void deleteSearchHistory(Long userId, String searchHistoryId) {
        UserSearchHistoryDocument userSearchHistoryDocument = userSearchHistoryRepository.findById(searchHistoryId)
                .orElseThrow(() -> new ApiException(ErrorCode.BAD_REQEUST, "해당하는 검색 기록이 없습니다."));
        try {
            userSearchHistoryRepository.delete(userSearchHistoryDocument);
        }catch (Exception e) {
            throw new ApiException(SearchHistoryError.SERVER_ERROR, "사용자 검색 기록 삭제 중 오류 발생");
        }
    }


    public void deleteAllUserSearchHistory(Long userId) {
        try {
            userSearchHistoryRepository.deleteAllByUserId(userId.toString());
        }catch (Exception e){
            throw new ApiException(SearchHistoryError.SERVER_ERROR, "사용자 검색 기록 삭제 중 오류 발생");
        }
    }
}