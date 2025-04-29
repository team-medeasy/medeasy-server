package com.medeasy.domain.chat.analyzer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medeasy.domain.chat.request_type.BasicRequestType;
import lombok.RequiredArgsConstructor;
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

@Service
@RequiredArgsConstructor
public class BasicPromptAnalyzer extends PromptAnalyzer {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${gemini.api.key}")
    private String apiKey;
    @Value("${gemini.api.url}")
    private String apiUrl;

    private String basicStatusTemplate= """
            # 행동 
            사용자의 채팅 내용을 보고 어떤 기능을 요청하는지 판별해줘.
            판별 기준은 어떤 기능을 요청하는지 정리한 enum의 이름이 담긴 request_type과 요청 종류를 판별할 조건 condition이 아래와 같은 형태로 제공될거야.
            
            예시:
            request_type: "ROUTINE_REGISTER", condition: "사용자가 정확히 루틴 등록 정보를 제공하지 않고, 일반적으로 루틴을 등록하고 싶다고 할 때"
            
            condition을 보고 제일 알맞는 request_type을 판별하여 응답 형태에 맞게 반환하면 돼.
            
            request_type 리스트: %s  
            
            # 응답 형태
            응답 형태는 아래 json 형식으로 작성해줘 
            json 형태 말고는 절대 어떠한 텍스트, 아이콘 등등이 들어가면 안돼 
            
            {
                "request_type": "ROUTINE_REGISTER"
                "message": "기본 루틴 등록, 처방전 루틴 등록, 알약 촬영 루틴 등록 중 어떤 루틴 등록을 원하시나요?"
            }
            
            필드 설명
            request_type: request_type의 원본 데이터인 enum의 이름
            message: 클라이언트에 제공할 메시지       
           
            """;


    public String analysisType(Long userId, String message){
        List<String> requestTypes = Arrays.stream(BasicRequestType.values())
                .map(rt -> String.format(
                        "request_type: \"%s\", condition: \"%s\"",
                        rt.getType(),
                        rt.getCondition()
                ))
                .toList();

        // prompt 관련
        String prompt= String.format(basicStatusTemplate, requestTypes);
        String finalPrompt = systemTemplate + prompt;

        return requestToAi(finalPrompt);
    }


    @Override
    String requestToAi(String finalPrompt) {
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
