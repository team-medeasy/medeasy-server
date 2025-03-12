package com.medeasy.domain.search.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.medeasy.common.error.SchedulerError;
import com.medeasy.common.exception.ApiException;
import com.medeasy.domain.search.dto.SearchPopularResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchPopularScheduler {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    private static final String queryJson = "{\n" +
            "  \"size\": 0,\n" +
            "  \"aggs\": {\n" +
            "    \"recent_popular_keywords\": {\n" +
            "      \"scripted_metric\": {\n" +
            "        \"init_script\": \"state.docs = []; state.now = System.currentTimeMillis(); state.scale = 3600000.0;\",\n" +
            "        \"map_script\": \"long docTime = doc['searchTime'].value.toInstant().toEpochMilli(); String kw = doc['keyword'].value; state.docs.add(['time': docTime, 'keyword': kw]);\",\n" +
            "        \"combine_script\": \"return state.docs;\",\n" +
            "        \"reduce_script\": \"def allDocs = new ArrayList(); for (s in states) { allDocs.addAll(s); }\\n" +
            "          long now = System.currentTimeMillis();\\n" +
            "          double scale = 3600000.0;\\n" +
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
    /**
     * 최근 1000개 문서를 대상으로 각 검색어의 시간 감쇠(decay) 합산 점수를 계산하여,
     * 상위 10개의 인기 검색어를 조회하는 쿼리를 실행.
     */
    @Scheduled(fixedRate = 60000) // 60000 밀리초 1분
    public void executeRecentPopularKeywordsQuery() {
        List<SearchPopularResponse> searchPopularResponses=null;

        // 인기 검색어 쿼리
        try {
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

            searchPopularResponses = objectMapper.readValue(topKeywords.toString(), new TypeReference<List<SearchPopularResponse>>() {});

        } catch (IOException e) {
            throw new ApiException(SchedulerError.SERVER_ERROR, "서버 스케줄러 인기 검색어 오류");
        }

        // 엘라스틱 서치에 다시 저장하는 코드
        try {
            log.info("인기 검색어 객체 결과: {}", searchPopularResponses);
        } catch (Exception e) {
            throw new ApiException(SchedulerError.SERVER_ERROR, "인기 검색어 저장 중 오류");
        }
    }
}

