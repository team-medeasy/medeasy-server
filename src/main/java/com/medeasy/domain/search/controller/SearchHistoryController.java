package com.medeasy.domain.search.controller;

import com.medeasy.common.annotation.UserSession;
import com.medeasy.common.api.Api;
import com.medeasy.domain.search.business.SearchHistoryBusiness;
import com.medeasy.domain.search.dto.SearchHistoryResponse;
import com.medeasy.domain.search.dto.SearchPopularResponse;
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
    @Operation(summary = "사용자 최근 검색어 조회 api", description =
            """
            사용자 최근 검색어 조회 api
            
            size 개수만큼 사용자 검색 기록을 조회한다. 
            
            반환값: 
            
            id: 문자열 -> 사용자 검색어 식별자 나중에 검색어 삭제 api 호출시 활용 
            
            keyword: 문자열 -> 사용자 검색 기록 키워드
            """
    )
    public Object getUserSearchHistory(
            @Parameter(hidden = true) @UserSession Long userId,
            @RequestParam(value = "size", defaultValue = "10", required = false)
            @Parameter(description = "불러올 최근 검색어 개수 (default: 10)", required = false)
            int size
    ) {
        List<SearchHistoryResponse> response=searchHistoryBusiness.getUserSearchHistories(userId, size);

        return Api.OK(response);
    }

    @DeleteMapping("/{search_history_id}")
    @Operation(summary = "사용자 최근 검색어 삭제 api", description =
            """
            사용자 최근 검색어 삭제 api
            
            사용자 검색어 조회 api를 통하여 조회된 문자열 id를 입력하여, 
            
            해당 검색어 삭제 
            """
    )
    public Object deleteUserSearchHistory(
            @Parameter(hidden = true) @UserSession Long userId,
            @PathVariable(name = "search_history_id")
            @Parameter(description = "검색 기록 id (문자열)", required = true)
            String searchHistoryId
    ) {
        searchHistoryBusiness.deleteUserSearchHistory(userId, searchHistoryId);

        return Api.OK(null);
    }

    @DeleteMapping("/all")
    @Operation(summary = "사용자 최근 검색어 전체 삭제 api", description =
            """
            사용자 최근 검색어 전체 삭제 api
            """
    )
    public Object deleteUserSearchHistory(
            @Parameter(hidden = true) @UserSession Long userId
    ) {
        searchHistoryBusiness.deleteAllUserSearchHistory(userId);

        return Api.OK(null);
    }

    @GetMapping("/popular")
    @Operation(summary = "인기 검색어 조회 api", description =
            """
            인기 검색어 조회 api
            
            인기 검색어를 순위 오름차순 기준으로 10개 조회 
            
            지수감쇠 함수를 통하여 검색 시간에 따라 순위 점수 차등 부여 (현재는 10시간 기준으로 급격히 점수 감소)
            
            ex) 검색한지 10시간이 지난 검색 기록들은 인기 검색어에 적게 반영된다.
            
            응답 필드 설명:
            
            id: 인기 검색어 문서 ID
                    
            rank: 현재 랭킹 
                    
            keyword: 검색 키워드
                    
            updated_at: 업데이트 시간 
                    
            rank_change: 1시간 전과 비교하여 랭킹 변화량
                    
            is_new_keyword: 랭킹에 새로 등장한 키워드 여부
            
            """
    )
    public Api<List<SearchPopularResponse>> getPopularSearchHistory(
    ) {
        var response=searchHistoryBusiness.getSearchPopularHistoriesList();

        return Api.OK(response);
    }
}
