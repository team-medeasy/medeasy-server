package com.medeasy.domain.ai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medeasy.domain.chat.status.SuperStatus;
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
public class ChatAiService {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${gemini.api.key}")
    private String apiKey;
    @Value("${gemini.api.url}")
    private String apiUrl;

    private String roopTemplate = """
            
            """;

    private String systemTemplate= """
            # 상황 
            너는 복약 루틴을 관리하는 앱 메디지의 AI 채팅봇 메디씨야.
            
            너의 역할은 사용자의 채팅을 보고 그에 맞는 채팅 타입을 유추하여 앱의 내부 기능과 연계하여 사용자에게 서비스를 제공하는 것이야
            """
            ;

    private String basicStatusTemplate= """
            # 행동 
            사용자의 채팅 내용을 보고 제공할 Chat Status List 목록을 보고 가장 잘 어울리는 List를 출력하면 돼.
            
            기본 Chat Status List: %s  
            
            # 응답 형태
            응답 형태는 아래 json 형식으로 작성해줘 
            json 형태 말고는 절대 어떠한 텍스트, 아이콘 등등이 들어가면 안돼 
            
            {
                chat_status: "routine_register",
                message: "기본 루틴 등록, 처방전 루틴 등록, 알약 촬영 루틴 등록 중 어떤 루틴 등록을 원하시나요?"
            }
            
            필드 설명
            chat_status: 제공한 chat_status_list 중 가장 잘 맞는 status
            message: 클라이언트에 제공할 메시지       
            
            # 분석 알고리즘 
            루틴을 등록하고 싶다와 같은 느낌의 텍스트 입력시 
            
            기본 루틴 등록, 처방전 루틴 등록, 알약 촬영 루틴 등록 중 원하는 루틴이 있는지 물어보는 거야 
            """;

    public String analysisInLoop() {
        boolean isStop = false;
        String context;

        while (!isStop) {

        }

        return null;
    }

    public void generateMessage(String message){

    }

    public String analysisType(String message){
        String url = apiUrl + apiKey;
        List<String> intents=Arrays.stream(SuperStatus.values()).map(SuperStatus::getIntent).toList();
        String prompt= String.format(basicStatusTemplate, intents);

        String finalPrompt = systemTemplate + prompt;

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

    /**
     * 채팅으로부터 어떠한 기능을 수행하면 좋을지 판별하는 메서드
     * */
    public void analysisMessage(List<String> memoryMessages, String message) {

    }
}
