package com.medeasy.domain.chat.analyzer;

import com.medeasy.domain.chat.dto.RoutineContext;
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
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultRoutinePromptAnalyzer extends PromptAnalyzer {
    private final RestTemplate restTemplate;
    private final Map<Long, RoutineContext> routineContext = new ConcurrentHashMap<>();

    @Value("${gemini.api.key}")
    private String apiKey;
    @Value("${gemini.api.url}")
    private String apiUrl;

    private String basicStatusTemplate= """
            # 행동 
            너는 기본 루틴 기능 담당 역할이야. 너한테는 서비스의 기본 루틴 등록에 접근할 수 있는 권한이 있어. 
            사용자로부터 필요한 복약 정보를 전부 수집하여 적절한 응답을 내려주는 것이 너의 목적이야
            
            # 사용자별 routine_context
            %s 
            
            # 조건 
            사용자의 메시지 정보와 routine_context 값을 종합하였을 때 
            ## 조건 1 
            routine_context의 필드값이 다 채워지지 않은 예정이라면
            응답의 request_type은 기존의 ROUTINE_REGISTER를 유지해주고 
            message는 null인 값에 대해서 재질의 해줘 
            
            ## 조건 2 
            routine_context의 필드값이 다 채워질 예정이라면,
            응답의 request_type은 요청을 처리하였다는 COMPLETED 값
            message는 루틴 등록을 완료 메시지를 줘
            
            기존 routine_context에서 추가 메시지로 채워진 routine_context 내용도 꼭 응답 형식에 맞게 포함시켜야해
             
            """;
    private String responseTemplate = """
            # 응답 예시
            응답 형태는 아래 json 형식과 같이 작성해줘 
            json 형태 말고는 절대 어떠한 텍스트, 아이콘 등등이 들어가면 안돼 
            
            {
                "request_type": "ROUTINE_REGISTER",
                "message": "기본 루틴 등록, 처방전 루틴 등록, 알약 촬영 루틴 등록 중 어떤 루틴 등록을 원하시나요?",
                "response_reason": "type을 판단한 이유는 ...",
                "routine_context": {
                    medicine_name: "아스피린",
                    interval_days: 1,
                    dose: 3,
                    schedule_names: ["아침", "점심"]
                }
            }
            """;

    private String requestJsonTemplate = """
            {
                "request_type": "%s",
                "condition" : "%s",
                "recommend_message" : "%s"
            }
            """;

    public String analysisType(Long userId, String message){
        RoutineContext userRoutineContext = routineContext.computeIfAbsent(userId, id -> new RoutineContext());

        List<String> requestTypes = Arrays.stream(RoutineRequestType.values())
                .map(rt -> String.format(
                        requestJsonTemplate,
                        rt.getType(),
                        rt.getCondition(),
                        rt.getRecommendMessage()
                ))
                .toList();

        // prompt 관련
        String prompt= String.format(basicStatusTemplate, userRoutineContext);
        String finalPrompt = systemTemplate +  prompt + responseTemplate ;

        log.info("prompt debug: {}", finalPrompt);

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
