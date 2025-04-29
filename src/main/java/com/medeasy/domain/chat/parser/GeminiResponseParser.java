package com.medeasy.domain.chat.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.medeasy.common.error.AiErrorCode;
import com.medeasy.common.exception.ApiException;
import com.medeasy.domain.chat.dto.AiChatResponse;
import com.medeasy.domain.chat.dto.RoutineAiChatResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class GeminiResponseParser {

    private final ObjectMapper objectMapper;

    public AiChatResponse parseGeminiResponse(String json) {
        try {
            log.info("json 형식 체크: {}", json);
            JsonNode rootNode = objectMapper.readTree(json);

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
            AiChatResponse aiChatResponse = objectMapper.readValue(cleanedJson, AiChatResponse.class);

            return aiChatResponse;

        } catch (Exception e) {
            throw new ApiException(AiErrorCode.PARSING_ERROR);
        }
    }

    public RoutineAiChatResponse parseRoutineGeminiResponse(String json) {
        try {
            log.info("json 형식 체크: {}", json);
            JsonNode rootNode = objectMapper.readTree(json);

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
            RoutineAiChatResponse aiChatResponse = objectMapper.readValue(cleanedJson, RoutineAiChatResponse.class);

            return aiChatResponse;

        } catch (Exception e) {
            throw new ApiException(AiErrorCode.PARSING_ERROR);
        }
    }
}
