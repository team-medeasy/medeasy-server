package com.medeasy.domain.chat.analyzer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommonPromptAnalyzer {
    @Value("${gemini.api.key}")
    private String apiKey;
    @Value("${gemini.api.url}")
    private String apiUrl;

    private RestTemplate restTemplate;

    public String requestToAi(
            String systemPrompt,           // AI의 기본 동작과 역할을 정의하는 프롬프트
            String responseTemplatePrompt, // 응답 형식을 지정하는 프롬프트
            String requestTemplatePrompt,  // 요청 형식을 지정하는 프롬프트
            String constraintPrompt,       // 행동 제약사항을 정의하는 프롬프트
            String contextPrompt           // 상황 설명 및 배경 정보를 제공하는 프롬프트
    ) {
        String finalPrompt = systemPrompt + responseTemplatePrompt + requestTemplatePrompt + constraintPrompt + contextPrompt;
        return sendRequest(finalPrompt);
    }

    private String sendRequest(String finalPrompt) {
        String url = apiUrl + apiKey;

        // 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 요청 바디 데이터 생성
        Map<String, Object> requestBody = new HashMap<>();
        Map<String, Object> content = new HashMap<>();
        Map<String, Object> part = new HashMap<>();

        part.put("text", finalPrompt);
        content.put("parts", new Map[]{part});
        requestBody.put("contents", new Map[]{content});

        // HTTP 요청 실행
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
        return restTemplate.postForObject(url, requestEntity, String.class);
    }
}
