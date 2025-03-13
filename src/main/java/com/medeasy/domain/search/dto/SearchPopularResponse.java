package com.medeasy.domain.search.dto;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchPopularResponse {
    private String id;

    private Integer rank;

    private String keyword;

    private Instant updatedAt;

    private Integer rankChange;

    private Boolean isNewKeyword;
}
