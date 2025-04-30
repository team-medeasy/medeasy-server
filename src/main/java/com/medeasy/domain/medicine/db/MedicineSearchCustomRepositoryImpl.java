package com.medeasy.domain.medicine.db;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch.core.mget.MultiGetError;
import co.elastic.clients.elasticsearch.core.mget.MultiGetOperation;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.medeasy.common.error.ErrorCode;
import com.medeasy.common.error.MedicineErrorCode;
import com.medeasy.common.exception.ApiException;
import com.medeasy.domain.search.db.SearchHistoryDocument;
import com.medeasy.domain.search.dto.SearchPopularDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.MultiGetItem;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.rmi.ServerError;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Repository
@AllArgsConstructor
public class MedicineSearchCustomRepositoryImpl implements MedicineSearchCustomRepository {

    private final ElasticsearchOperations elasticsearchOperations;
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

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
                        .query(itemName)
                        .fuzziness("1")
                    )
                );
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

    /**
     * 처방전 약품 검색시 처음 사용되는 메서드
     * EDI_CODE를 통해 정확히 매칭되는 약품을 찾는다.
     * */
    @Override
    public MedicineDocument findByEdiCode(String ediCode) {
        Query boolQuery=QueryBuilders.bool(boolQueryBuilder ->
            boolQueryBuilder.must(
                    queryBuilder -> queryBuilder.term(
                            termQueryBuilder -> termQueryBuilder.field("edi_code").value(ediCode)
                    )
            )
        );

        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(boolQuery)
                .withPageable(Pageable.ofSize(1))
                .build()
                ;

        SearchHits<MedicineDocument> searchHits=elasticsearchOperations.search(nativeQuery, MedicineDocument.class);

        return searchHits.getSearchHits()
                .getFirst().getContent();
    }


    /**
     * EDI_CODE와 ITEM_NAME 기반으로 약 검색하는 메서드
     * 처방전 검색시 EDI_CODE와 일치하는 약이 존재하지 않을 경우 유사한 약이라도 찾아서 출력
     * */
    @Override
    public List<MedicineDocument> findMedicineByEdiCodeAndItemName(String ediCode, String itemName, Pageable pageable) {
        Query booQuery=QueryBuilders.bool(boolQueryBuilder ->
            boolQueryBuilder.should(objectBuilder -> {
                objectBuilder.term(termQueryBuilder ->
                        termQueryBuilder.field("edi_code").value(ediCode)
                );

                objectBuilder.match(matchQueryBuilder ->
                    matchQueryBuilder.field("item_name").query(itemName)
                );
                return objectBuilder;
            })
        );

        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(booQuery)
                .withPageable(pageable)
                .build()
                ;

        SearchHits<MedicineDocument> searchHits = elasticsearchOperations.search(nativeQuery, MedicineDocument.class);

        return searchHits.getSearchHits()
                .stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }

    @Override
    public List<MedicineDocument> findSimilarMedicines(String className, List<String> indications, Pageable pageable) {
        String indication = indications.toString();
        Query boolQuery=QueryBuilders.bool(boolQueryBuilder -> {
            boolQueryBuilder.must(queryBuilder ->
                queryBuilder.term(termQuery ->
                        termQuery.field("class_name")
                                .value(className)
                )
            );

            boolQueryBuilder.should(queryBuilder ->
                queryBuilder.match(matchQuery->
                    matchQuery.field("indications")
                            .query(indication)
                )
            );
            return boolQueryBuilder;
        });

        NativeQuery nativeQuery=NativeQuery.builder()
                .withQuery(boolQuery)
                .withPageable(pageable)
                .build()
                ;

        SearchHits<MedicineDocument> searchHits = elasticsearchOperations.search(nativeQuery, MedicineDocument.class);

        return searchHits.getSearchHits()
                .stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }

    @Override
    public List<MedicineDocument> findMedicinesByIds(List<String> ids) {
        Query idsQuery=QueryBuilders.ids(idsQueryBuilder -> idsQueryBuilder.values(ids));

        NativeQuery nativeQuery=NativeQuery.builder()
                .withQuery(idsQuery)
                .build()
                ;

        SearchHits<MedicineDocument> searchHits = elasticsearchOperations.search(nativeQuery, MedicineDocument.class);

        return searchHits.getSearchHits()
                .stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }

    @Override
    public List<MedicineDocument> findMedicinesByMget(List<String> ids) {
        try {
            String queryJson = objectMapper.writeValueAsString(Map.of("ids", ids));

            Request request = new Request("GET", "/medicine_data/_mget");
            request.setJsonEntity(queryJson);

            Response response = restClient.performRequest(request);
            String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode docs = root.path("docs");

            List<MedicineDocument> result = new ArrayList<>();
            for (JsonNode doc : docs) {
                if (doc.path("found").asBoolean()) {
                    JsonNode id = doc.path("_id");
                    JsonNode source = doc.path("_source");

                    MedicineDocument medicine = objectMapper.treeToValue(source, MedicineDocument.class);
                    medicine.setId(id.asText());
                    result.add(medicine);
                }else{
                    log.error("잘못된 Medicine Id값 요청: {}", doc.path("_id").asText());
                }
            }

            return result;
        }catch (Exception e){
            throw new ApiException(ErrorCode.SERVER_ERROR);
        }
    }

    /**
     * medicine_data document에 audio_url 업데이트
     * */
    @Override
    public void updateMedicineAudioUrl(String medicineId, String audioUrl) {
        UpdateQuery updateQuery = UpdateQuery.builder(medicineId)
                .withDocument(Document.create().append("audio_url", audioUrl))
                .build();

        elasticsearchOperations.update(updateQuery, IndexCoordinates.of("medicine_data"));
    }

    @Override
    public Optional<MedicineDocument> findFirstMedicineByName(String medicineName) {
        Query boolQuery = QueryBuilders.bool(boolQueryBuilder ->
                boolQueryBuilder.must(queryBuilder ->
                        queryBuilder.match(matchQueryBuilder ->
                            matchQueryBuilder.field("item_name")
                                    .query(medicineName)
                        )
                )
        );

        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(boolQuery)
                .withPageable(Pageable.ofSize(1))
                .build()
                ;

        SearchHits<MedicineDocument> searchHits = elasticsearchOperations.search(nativeQuery, MedicineDocument.class);

        return searchHits.getSearchHits()
                .stream()
                .map(SearchHit::getContent)
                .findFirst();
    }

}
