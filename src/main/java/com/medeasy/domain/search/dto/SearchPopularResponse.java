package com.medeasy.domain.search.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchPopularResponse {
    private double score;

    private String keyword;
}
