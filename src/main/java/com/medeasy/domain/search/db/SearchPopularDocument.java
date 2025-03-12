package com.medeasy.domain.search.db;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "search_popular")
public class SearchPopularDocument {
    @Id
    private String id;

    @Field(type=FieldType.Integer)
    private Integer rank;

    @Field(type=FieldType.Keyword)
    private String keyword;

    @Field(type=FieldType.Date)
    private Instant updatedAt;
}
