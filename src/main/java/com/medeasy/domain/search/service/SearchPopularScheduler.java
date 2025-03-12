package com.medeasy.domain.search.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.medeasy.common.error.SchedulerError;
import com.medeasy.common.exception.ApiException;
import com.medeasy.domain.search.db.SearchPopularDocument;
import com.medeasy.domain.search.db.SearchPopularRepository;
import com.medeasy.domain.search.dto.SearchPopularResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchPopularScheduler {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final SearchPopularRepository searchPopularRepository;

    public void getQueryJson() {

    }
    /**
     * 최근 1000개 문서를 대상으로 각 검색어의 시간 감쇠(decay) 합산 점수를 계산하여,
     * 상위 10개의 인기 검색어를 조회하는 쿼리를 실행.
     */
    @Transactional
    @Scheduled(fixedRate = 60000) // 60000 밀리초 1분
    public void executeRecentPopularKeywordsQuery() {
        long now = Instant.now().toEpochMilli();
        long oneHourAgo=now-3600000;
        log.info("현재 밀리세컨드: {}", now);

        List<SearchPopularResponse> nowSearchPopularResponse=getPopularKeywordsByMilliSeconds(now);
        List<SearchPopularResponse> oneHourAgoSearchPopularResponse=getPopularKeywordsByMilliSeconds(oneHourAgo);

        // 엘라스틱 서치에 다시 저장하는 코드
        try {
            log.info("인기 검색어 객체 결과: {}", nowSearchPopularResponse);
            Instant instantNow = Instant.now();

            List<SearchPopularDocument> documents = IntStream.range(0, nowSearchPopularResponse.size())
                    .mapToObj(index -> SearchPopularDocument.builder()
                            .rank(index + 1) // 리스트 순서대로 rank 지정 (1부터 시작)
                            .keyword(nowSearchPopularResponse.get(index).getKeyword()) // 검색어 삽입
                            .updatedAt(instantNow) // 현재 시간 삽입
                            .build())
                    .toList();

            // Elasticsearch 저장 로직 추가
            searchPopularRepository.saveAll(documents);
            log.info("인기 검색어 저장 완료 1등 검색어: {}", documents.getFirst().getKeyword());
        } catch (Exception e) {
            throw new ApiException(SchedulerError.SERVER_ERROR, "인기 검색어 저장 중 오류");
        }
    }

    public List<SearchPopularResponse> getPopularKeywordsByMilliSeconds(long milliSeconds) {
        try {
            /**
             *  scale로 감쇠 속도 지정 가능
             *  scale 36000000 -> 10시간 기준
             * */
            String queryJson = "{\n" +
                    "  \"size\": 0,\n" +
                    "  \"aggs\": {\n" +
                    "    \"recent_popular_keywords\": {\n" +
                    "      \"scripted_metric\": {\n" +
                    "        \"init_script\": \"state.docs = [];\",\n" +
                    "        \"map_script\": \"long docTime = doc['searchTime'].value.toInstant().toEpochMilli(); String kw = doc['keyword'].value; state.docs.add(['time': docTime, 'keyword': kw]);\",\n" +
                    "        \"combine_script\": \"return state.docs;\",\n" +
                    "        \"reduce_script\": \"def allDocs = new ArrayList(); for (s in states) { allDocs.addAll(s); }\\n" +
                    "          long now = "+milliSeconds+"L;\\n" +
                    "          double scale = 36000000.0;\\n" +
                    "          allDocs.sort((a,b) -> {\\n" +
                    "            long tA = (long)a.time;\\n" +
                    "            long tB = (long)b.time;\\n" +
                    "            if (tB > tA) { return 1; } else if (tB < tA) { return -1; } else { return 0; }\\n" +
                    "          });\\n" +
                    "          if (allDocs.size() > 1000) {\\n" +
                    "            allDocs = allDocs.subList(0, 1000);\\n" +
                    "          }\\n" +
                    "          def keywordScores = [:];\\n" +
                    "          for (doc in allDocs) {\\n" +
                    "            long docTime = (long) doc.time;\\n" +
                    "            String keyword = (String) doc.keyword;\\n" +
                    "            long diff = now - docTime;\\n" +
                    "            double decay = Math.exp(- diff / scale);\\n" +
                    "            double oldVal = keywordScores.containsKey(keyword) ? keywordScores[keyword] : 0.0;\\n" +
                    "            keywordScores[keyword] = oldVal + decay;\\n" +
                    "          }\\n" +
                    "          def resultList = [];\\n" +
                    "          for (entry in keywordScores.entrySet()) {\\n" +
                    "            resultList.add(['keyword': entry.getKey(), 'score': entry.getValue()]);\\n" +
                    "          }\\n" +
                    "          resultList.sort((a,b) -> {\\n" +
                    "            double vA = (double)a.score;\\n" +
                    "            double vB = (double)b.score;\\n" +
                    "            if (vB > vA) { return 1; } else if (vB < vA) { return -1; } else { return 0; }\\n" +
                    "          });\\n" +
                    "          if (resultList.size() > 10) {\\n" +
                    "            resultList = resultList.subList(0, 10);\\n" +
                    "          }\\n" +
                    "          return ['topKeywords': resultList];\"\n" +
                    "      }\n" +
                    "    }\n" +
                    "  }\n" +
                    "}";
            Request request = new Request("GET", "/search_history/_search");

            request.setJsonEntity(queryJson);
            Response response = restClient.performRequest(request);
            String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode topKeywords = root.path("aggregations")
                    .path("recent_popular_keywords")
                    .path("value")
                    .path("topKeywords");

            log.info("파싱 부분: {}", topKeywords);

            return objectMapper.readValue(topKeywords.toString(), new TypeReference<List<SearchPopularResponse>>() {});
        }catch (Exception e){
            throw new ApiException(SchedulerError.SERVER_ERROR, "인기 검색어 조회 중 오류");
        }
    }
}

