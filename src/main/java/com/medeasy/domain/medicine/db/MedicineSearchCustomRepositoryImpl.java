package com.medeasy.domain.medicine.db;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import com.medeasy.domain.search.db.SearchHistoryDocument;
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

    /**
     *
     *
     * GET medicine_index/_search
     * {
     *   "query": {
     *     "bool": {
     *       "must": [
     *         {
     *           "match": {
     *             "itemName": "아스피린"
     *           }
     *         },
     *         {
     *           "bool": {
     *             "should": [
     *               { "term": { "color": "하양" } },
     *               { "term": { "color": "노랑" } },
     *               { "term": { "color": "파랑" } }
     *             ],
     *             "minimum_should_match": 1
     *           }
     *         },
     *         {
     *           "bool": {
     *             "should": [
     *               { "term": { "shape": "원형" } },
     *               { "term": { "shape": "삼각형" } }
     *             ],
     *             "minimum_should_match": 1
     *           }
     *         }
     *       ]
     *     }
     *   },
     *   "size": 10
     * }
     *
     * TODO 제약 회사 이름으로도 검색 추가 itemName -> searchString
     *
     * */
    @Override
    public List<MedicineDocument> findMedicineBySearching(String itemName, List<String> colors, List<String> shapes, Pageable pageable) {
        Query boolQuery = QueryBuilders.bool(boolQueryBuilder -> {
            if (itemName != null && !itemName.isEmpty()) {
                boolQueryBuilder.must(QueryBuilder -> QueryBuilder.match(matchQueryBuilder -> matchQueryBuilder
                        .field("item_name")
                        .query(itemName)));
            }


            if (colors != null && !colors.isEmpty()) {
                Query colorQuery = QueryBuilders.bool(colorBool ->
                        colorBool.should(colors.stream()
                                .map(color -> QueryBuilders.term(termQueryBuilder -> termQueryBuilder
                                        .field("color_classes")
                                        .value(color)))
                                .toList()
                        ).minimumShouldMatch("1")  // ✅ Colors에서 최소 1개 만족
                );
                boolQueryBuilder.must(colorQuery);
            }

            // Shapes 관련 조건을 별도의 boolQuery로 처리 (최소 1개 만족)
            if (shapes != null && !shapes.isEmpty()) {
                Query shapeQuery = QueryBuilders.bool(shapeBool ->
                        shapeBool.should(shapes.stream()
                                .map(shape -> QueryBuilders.term(termQueryBuilder -> termQueryBuilder
                                        .field("drug_shape")
                                        .value(shape)))
                                .toList()
                        ).minimumShouldMatch("1")  // ✅ Shapes에서 최소 1개 만족
                );
                boolQueryBuilder.must(shapeQuery);
            }

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

    public void saveSearchHistory(SearchHistoryDocument searchHistoryDocument){
        elasticsearchOperations.save(searchHistoryDocument);
    }
}
