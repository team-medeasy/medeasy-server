package com.medeasy.domain.chat.analyzer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medeasy.domain.chat.db.UserSession;
import com.medeasy.domain.chat.request_type.BasicRequestType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
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
        {
            "request_type": "요청 기능 종류",
            "condition" : "기능을 선택할 조건",
            "recommendMessage" : "사용자에게 추천할 메시지"
        }
        
        # 매우 중요한 request_type 매핑 규칙
        condition을 보고 사용자 메시지와 제일 알맞는 request_type을 판별하여 아래 응답 형태에 맞게 반환하면 돼.
        condition에 제시된 예시 문구를 참고하여 판단하고, 정확히 일치하지 않더라도 유사한 의미를 내포하고 있다면 해당 request_type으로 판단해줘.
        
        request_type 리스트: %s
        
        # 응답 형태
        {
            "request_type": "판별된 request_type enum 이름",
            "message": "해당 request_type의 recommendMessage 값"
        }
        
        # 주의사항
        - request_type 리스트의 condition을 **정확히** 이해하고 분류해야 해.
        - 예를 들어 '기본 루틴 등록하고 싶어'라는 메시지에는 반드시 DEFAULT_ROUTINE_REGISTER를 반환해야 해. '루틴'이라는 단어가 들어갔다고 해서 ROUTINE_REGISTER로 잘못 판단하지 않도록 주의해줘.
        - 사용자가 의약품 정보를 얻고 싶어할 때는 MEDICINE_SEARCH를 반환해야 해. '약'이나 '의약품'과 관련된 단어가 있다면 MEDICINE_SEARCH일 가능성이 높아.
        
        """;


    public String analysisType(UserSession userSession, String message){
        List<String> requestTypes = Arrays.stream(BasicRequestType.values())
                .map(rt -> String.format(
                        "request_type: \"%s\", condition: \"%s\", recommend_message: \"%s\"",
                        rt.getType(),
                        rt.getCondition(),
                        rt.getRecommendMessage()
                ))
                .toList();

        // prompt 관련
        String prompt= String.format(basicStatusTemplate, requestTypes);
        String finalPrompt = responseTemplate + systemTemplate + prompt;

        log.info("사용자 요청 분류 최종 프롬프트: {}", finalPrompt);

        return requestToAi(finalPrompt);
    }


    @Override
    String requestToAi(String finalPrompt) {
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
