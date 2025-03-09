package com.medeasy.domain.search.controller;

import com.medeasy.common.annotation.UserSession;
import com.medeasy.domain.search.business.SearchHistoryBusiness;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
        List<String> response=searchHistoryBusiness.getUserSearchHistories(userId);

        return ResponseEntity.ok()
                .body("save successful")
                .toString();
    }
}
