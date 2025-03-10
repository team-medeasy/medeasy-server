package com.medeasy.domain.search.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchHistoryResponse {

    private String id;

    private String keyword;
}
