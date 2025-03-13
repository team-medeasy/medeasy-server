package com.medeasy.domain.search.business;

import com.medeasy.common.annotation.Business;
import com.medeasy.domain.search.db.SearchHistoryDocument;
import com.medeasy.domain.search.db.SearchPopularDocument;
import com.medeasy.domain.search.db.UserSearchHistoryDocument;
import com.medeasy.domain.search.dto.SearchHistoryResponse;
import com.medeasy.domain.search.dto.SearchPopularResponse;
import com.medeasy.domain.search.service.SearchHistoryService;
import com.medeasy.domain.search.service.UserSearchHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Business
@RequiredArgsConstructor
public class SearchHistoryBusiness {

    private final SearchHistoryService searchHistoryService;
    private final UserSearchHistoryService userSearchHistoryService;

    /**
     * 검색 키워드 저장
     * 1. 인기 검색어 순위 분석을 위한 데이터
     * 2. 사용자 최근 검색어 관리를 위한 데이터
     * */
    public void saveSearchKeyword(String userId, String medicineName) {
        // 시스템 분석을 위한 데이터
        searchHistoryService.saveSearchKeyword(userId, medicineName);
        // 사용자 검색 기록 관리
        userSearchHistoryService.saveSearchKeyword(userId, medicineName);
    }

    /**
     * 1. 사용자 검색어 인덱스에서 기록 가져오는 함수
     * */
    public List<SearchHistoryResponse> getUserSearchHistories(Long userId, int size) {
        List<UserSearchHistoryDocument> histories = userSearchHistoryService.getUserSearchHistories(userId, size);

        return histories.stream().map(userSearchHistoryDocument -> {
            return SearchHistoryResponse.builder()
                    .id(userSearchHistoryDocument.getId())
                    .keyword(userSearchHistoryDocument.getKeyword())
                    .build();
        }).toList();
    }

    /**
     * 사용자 검색어 삭제 함수
     * */
    public void deleteUserSearchHistory(Long userId, String searchHistoryId) {
        userSearchHistoryService.deleteSearchHistory(userId, searchHistoryId);
    }

    /**
     * 사용자 검색어 전체 삭제 함수
     * */
    public void deleteAllUserSearchHistory(Long userId) {
        userSearchHistoryService.deleteAllUserSearchHistory(userId);

        log.info("사용자 검색 기록 전체 삭제 완료 userId: {}", userId);
    }

    /**
     * 인기 검색어 리스트 조회 함수
     * 1. 인기 검색어 분석 데이터 SearchPopular 인덱스에서 가장 최근 업데이트 시간 조회
     * 2. 업데이트 시간 기준으로 데이터 조회
     *
     * 하나의 쿼리로 실행하는 것이 베스트이나, 기술 문제로 나눠서 구현.
     * */
    public List<SearchPopularResponse> getSearchPopularHistoriesList() {
        String latestUpdatedAt=searchHistoryService.getLatestPopularUpdatedTime();
        List<SearchPopularDocument> searchPopularDocuments = searchHistoryService.getSearchPopularByDate(latestUpdatedAt);

        return searchPopularDocuments.stream().map(searchPopularDocument -> {
            return SearchPopularResponse.builder()
                    .id(searchPopularDocument.getId())
                    .rank(searchPopularDocument.getRank())
                    .keyword(searchPopularDocument.getKeyword())
                    .isNewKeyword(searchPopularDocument.getIsNewKeyword())
                    .updatedAt(searchPopularDocument.getUpdatedAt())
                    .rankChange(searchPopularDocument.getRankChange())
                    .build()
                    ;
        }).toList();
    }
}
