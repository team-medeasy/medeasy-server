package com.medeasy.domain.search.business;

import com.medeasy.common.annotation.Business;
import com.medeasy.domain.medicine.db.SearchHistoryDocument;
import com.medeasy.domain.medicine.service.MedicineDocumentService;
import com.medeasy.domain.search.service.SearchHistoryService;
import lombok.RequiredArgsConstructor;
import java.util.List;

@Business
@RequiredArgsConstructor
public class SearchHistoryBusiness {

    private final SearchHistoryService searchHistoryService;

    public void saveSearchKeyword(String userId, String medicineName) {
        searchHistoryService.saveSearchKeyword(userId, medicineName);
    }

    public List<String> getUserSearchHistories(Long userId, int size) {
        List<SearchHistoryDocument> histories = searchHistoryService.getUserSearchHistories(userId, size);

        return histories.stream().map(SearchHistoryDocument::getKeyword).toList();
    }
}
