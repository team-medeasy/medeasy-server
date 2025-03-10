package com.medeasy.domain.search.controller;

import com.medeasy.common.annotation.UserSession;
import com.medeasy.common.api.Api;
import com.medeasy.domain.search.business.SearchHistoryBusiness;
import com.medeasy.domain.search.dto.SearchHistoryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/search-history")
public class SearchHistoryController {

    private final SearchHistoryBusiness searchHistoryBusiness;

    @GetMapping("")
    @Operation(summary = "사용자 최근 검색어 조회 api", description = "사용자 최근 검색어 조회 api")
    public Object getUserSearchHistory(
            @Parameter(hidden = true) @UserSession Long userId,
            @RequestParam(value = "size", defaultValue = "10", required = false)
            @Parameter(description = "불러올 최근 검색어 개수 (default: 10)", required = false)
            int size
    ) {
        List<SearchHistoryResponse> response=searchHistoryBusiness.getUserSearchHistories(userId, size);

        return Api.OK(response);
    }

    @DeleteMapping("")
    @Operation(summary = "사용자 최근 검색어 삭제 api", description = "사용자 최근 검색어 삭제 api")
    public Object deleteUserSearchHistory(
            @Parameter(hidden = true) @UserSession Long userId,
            @RequestParam(value = "search_history_id", required = true)
            @Parameter(description = "검색 기록 id (문자열)", required = true)
            String searchHistoryId
    ) {
        searchHistoryBusiness.deleteUserSearchHistory(userId, searchHistoryId);

        return Api.OK(null);
    }
}
