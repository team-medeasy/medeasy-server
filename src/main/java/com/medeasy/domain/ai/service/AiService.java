package com.medeasy.domain.ai.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.medeasy.common.error.AiErrorCode;
import com.medeasy.common.error.ErrorCode;
import com.medeasy.common.exception.ApiException;
import com.medeasy.domain.ai.dto.AiResponseDto;
import com.medeasy.domain.ocr.dto.OcrParsedDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AiService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private final String promptTemplate = """
            # 설명
            아래에 json 텍스트를 하나 제공할거야
            그 텍스트는 OCR 기술을 사용하여 표로 이루어진 처방전 사진을 분석한 텍스트와,
            그 텍스트의 좌표 데이터가 들어있어.
            여기서 필요한 데이터만 추출, 나열할거야.
                        
            # 규칙
            1. 처방의약품의 명칭, 1회 투약량, 1일 투여횟수, 총투약일수, 용법에 해당하는 필드가 우리가 추출할 필드야.
            2. 응답에는 출력형태만 포함되도록 작성해줘.
            3. 출력형태의 각 필드는 다음 정보를 포함해야해:
                - name: 처방의약품의 명칭
                - dose: 1회 투약량
                - type_count: 1일 투여횟수
                - total_days: 총 투약일수
                - use_method: 용법
            4. 처방전에 포함된 약의 이름을 빠짐없이, 전부 추출해야해. 간혹 약의 이름인지 헷갈릴 수도 있어.
               이 Json 데이터는 약의 정보가 순서대로 나오기 때문에, 투약량을 나타내는 숫자 옆에 항상 약의 이름이 존재할 거야.
               이 부분을 참고해서 정확한 응답을 내려줘.

            # 입력 JSON 데이터:
            %s

            # 출력형태:
            {
            	"results": [
            		{
            			"name": "의약품 이름1",
            			"dose": 1,
            			"type_count": 2,
            			"total_days": 7,
            			"use_method": "용법"
            		},
            		{
            			"name": "의약품 이름2",
            			"dose": 1,
            			"type_count": 2,
            			"total_days": 7,
            			"use_method": "용법"
            		}
            	]
            }
            """;

    public String analysis(List<OcrParsedDto> ocrParsedDtos) {
        String url = apiUrl + apiKey;

        try {
            // OCR 분석 결과를 JSON 문자열로 변환
            String ocrJson = objectMapper.writeValueAsString(ocrParsedDtos);

            // OCR 데이터를 프롬프트에 포함
            String finalPrompt = String.format(promptTemplate, ocrJson);

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

        } catch (JsonProcessingException e) {
            throw new ApiException(AiErrorCode.PARSING_ERROR, "OCR 데이터를 JSON으로 변환하는 중 오류 발생"+e);
        }
    }

    public AiResponseDto parseGeminiResponse(String geminiResponse) {
        try {
            JsonNode rootNode = objectMapper.readTree(geminiResponse);

            // 토큰 수 추출
            Integer totalTokenCount = rootNode.path("usageMetadata").path("totalTokenCount").asInt();

            // JSON 텍스트 파싱
            String jsonResponse = rootNode.path("candidates")
                    .path(0)
                    .path("content")
                    .path("parts")
                    .path(0)
                    .path("text")
                    .asText();

            // gemini text 응답에 포함되어 있는 JSON 코드 블록 제거 및 빈칸 제거
            String cleanedJson = jsonResponse.replaceAll("```json|```", "").trim();

            JsonNode parsedJson = objectMapper.readTree(cleanedJson);
            List<AiResponseDto.DoseDto> doseDtos = objectMapper
                    .readerForListOf(AiResponseDto.DoseDto.class)
                    .readValue(parsedJson.path("results"))
                    ;

            return AiResponseDto.builder()
                    .totalTokenCount(totalTokenCount)
                    .doseDtos(doseDtos)
                    .build()
                    ;

        } catch (Exception e) {
            throw new ApiException(AiErrorCode.PARSING_ERROR);
        }
    }
}

