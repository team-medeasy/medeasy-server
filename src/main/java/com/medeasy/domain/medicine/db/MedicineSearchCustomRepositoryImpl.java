package com.medeasy.domain.medicine.db;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@AllArgsConstructor
public class MedicineSearchCustomRepositoryImpl implements MedicineSearchCustomRepository {

    private final ElasticsearchOperations elasticsearchOperations;

    @Override
    public List<MedicineDocument> findMedicineBySearching(String itemName, List<String> colors, String shape, Pageable pageable) {
        Query boolQuery = QueryBuilders.bool(boolQueryBuilder -> {
            if (itemName != null && !itemName.isEmpty()) {
                boolQueryBuilder.must(QueryBuilder -> QueryBuilder.match(matchQueryBuilder -> matchQueryBuilder
                        .field("itemName")
                        .query(itemName)));
            }

            if (colors != null && !colors.isEmpty()) {
                List<Query> colorQueries = colors.stream()
                        .map(color -> QueryBuilders.term(termQueryBuilder -> termQueryBuilder
                                .field("color")
                                .value(color)))
                        .toList();
                boolQueryBuilder.should(colorQueries);
            }

            boolQueryBuilder.minimumShouldMatch("1");

            return boolQueryBuilder;
        });

        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(boolQuery)
                .withPageable(pageable)
                .build()
                ;

        SearchHits<MedicineDocument> searchHits=elasticsearchOperations.search(nativeQuery, MedicineDocument.class);

        return searchHits.getSearchHits()
                .stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());

    }

}
