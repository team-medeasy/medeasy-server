package com.medeasy.domain.medicine.db;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@AllArgsConstructor
public class MedicineSearchCustomRepositoryImpl implements MedicineSearchCustomRepository {

    private final ElasticsearchOperations elasticsearchOperations;

    @Override
    public List<MedicineDocument> findByItemNameAndColor(String itemName, String colors, Pageable pageable) {
//        BoolQuery boolQuery = QueryBuilders.bool()
//                .must(QueryBuilders.match())
//                .should(QueryBuilders.terms("color.keyword", colors))
//                .minimumShouldMatch(1);

        return null;
    }
}
