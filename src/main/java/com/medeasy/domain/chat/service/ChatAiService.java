package com.medeasy.domain.chat.service;

import com.medeasy.common.api.Api;
import com.medeasy.domain.ai.dto.AiChatResponse;
import com.medeasy.domain.ai.service.GeminiResponseParser;
import com.medeasy.domain.chat.analyzer.PromptAnalyzer;
import com.medeasy.domain.chat.dto.ChatResponse;
import com.medeasy.domain.chat.request_type.BasicRequestType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatAiService {

    private final GeminiResponseParser geminiResponseParser;

    public AiChatResponse doRequest(PromptAnalyzer promptAnalyzer, String clientMessage) {
        try {
            // 1. 사용자 메시지를 AI로 분석
            String aiJsonResponse = promptAnalyzer.analysisType(clientMessage);

            // 2. AI 응답 파싱 (AiChatResponse 객체)
            return geminiResponseParser.parseGeminiResponse(aiJsonResponse);
        } catch (Exception e) {
            log.error("AI 분석 및 파싱 실패", e);
        }
        return null;
    }
}
