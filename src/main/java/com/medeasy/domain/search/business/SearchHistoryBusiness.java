package com.medeasy.domain.search.business;

import com.medeasy.common.annotation.Business;
import com.medeasy.domain.search.db.SearchHistoryDocument;
import com.medeasy.domain.search.db.SearchPopularDocument;
import com.medeasy.domain.search.dto.SearchHistoryResponse;
import com.medeasy.domain.search.dto.SearchPopularResponse;
import com.medeasy.domain.search.service.SearchHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Business
@RequiredArgsConstructor
public class SearchHistoryBusiness {

    private final SearchHistoryService searchHistoryService;

    public void saveSearchKeyword(String userId, String medicineName) {
        searchHistoryService.saveSearchKeyword(userId, medicineName);
    }

    public List<SearchHistoryResponse> getUserSearchHistories(Long userId, int size) {
        List<SearchHistoryDocument> histories = searchHistoryService.getUserSearchHistories(userId, size);

        return histories.stream().map(searchHistoryDocument -> {
            return SearchHistoryResponse.builder()
                    .id(searchHistoryDocument.getId())
                    .keyword(searchHistoryDocument.getKeyword())
                    .build();
        }).toList();
    }

    public void deleteUserSearchHistory(Long userId, String searchHistoryId) {
        searchHistoryService.deleteSearchHistory(userId, searchHistoryId);
    }

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

    public void deleteAllUserSearchHistory(Long userId) {
        searchHistoryService.deleteAllUserSearchHistory(userId);

        log.info("사용자 검색 기록 전체 삭제 완료 userId: {}", userId);
    }
}
