package com.medeasy.domain.chat.analyzer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medeasy.domain.chat.request_type.BasicRequestType;
import com.medeasy.domain.chat.request_type.RoutineRequestType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoutinePromptAnalyzer extends PromptAnalyzer {
    private final RestTemplate restTemplate;

    @Value("${gemini.api.key}")
    private String apiKey;
    @Value("${gemini.api.url}")
    private String apiUrl;

    private String basicStatusTemplate= """
            # 행동 
            사용자의 채팅 내용을 보고 어떤 기능을 요청하는지 판별해줘.
            판별 기준은 어떤 기능을 요청하는지 정리한 enum의 이름이 담긴 request_type과 요청 종류를 판별할 조건 condition이 아래와 같은 형태로 제공될거야.
            
            예시:
            {
                "request_type": "요청 기능 종류",
                "condition" : "기능을 선택할 조건",
                "recommend_message" : "사용자에게 제공할 메시지 예시"
            }
            
            condition을 보고 사용자 메시지와 제일 알맞는 request_type을 판별하여 응답 형태에 맞게 반환하면 돼.
            
            request_type 리스트: %s  
            """;

    private String specificTemplate= """
            # 추가 조건 
            request_type을 정했으면, 그에 맞는 recommend_message를 응답 json필드의 message에 매칭
            특히, 사용자가 "처방전", "사진", "알약" 단어를 언급하는 경우에는 DEFAULT가 아닌 정확히 PRESCRIPTION 또는 PILLS_PHOTO로 매칭해줘.
            """;

    public String analysisType(Long userId, String message){
        List<String> requestTypes = Arrays.stream(RoutineRequestType.values())
                .map(rt -> String.format(
                        requestJsonTemplate,
                        rt.getType(),
                        rt.getCondition(),
                        rt.getRecommendMessage()
                ))
                .toList();

        // prompt 관련
        String prompt= String.format(basicStatusTemplate, requestTypes);
        String finalPrompt = systemTemplate + prompt + responseTemplate + specificTemplate;

        log.info("prompt debug: {}", finalPrompt);

        return requestToAi(finalPrompt);
    }


    @Override
    public String requestToAi(String finalPrompt) {
        String url = apiUrl + apiKey;

        // 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 요청 바디 생성
        Map<String, Object> part = Map.of("text", finalPrompt);
        Map<String, Object> content = Map.of("parts", List.of(part));
        Map<String, Object> generationConfig = Map.of(
                "temperature", 0.5,
                "top_p", 0.2,
                "top_k", 10
        );

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(content),
                "generationConfig", generationConfig
        );

        // HTTP 요청 실행
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
        return restTemplate.postForObject(url, requestEntity, String.class);
    }

}
