package com.medeasy.domain.chat.service;

import com.medeasy.domain.chat.db.UserSession;
import com.medeasy.domain.chat.dto.RoutineAiChatResponse;
import com.medeasy.domain.chat.parser.GeminiResponseParser;
import com.medeasy.domain.chat.analyzer.PromptAnalyzer;
import com.medeasy.domain.chat.dto.AiChatResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RoutineChatAiService {

    private final GeminiResponseParser geminiResponseParser;

    public RoutineAiChatResponse doRequest(PromptAnalyzer promptAnalyzer, UserSession userSession, String clientMessage) {
        try {
            // 1. 사용자 메시지를 AI로 분석
            userSession.getMessages().add(clientMessage);
            String aiJsonResponse = promptAnalyzer.analysisType(userSession, clientMessage);

            // 2. AI 응답 파싱 (AiChatResponse 객체)
            return geminiResponseParser.parseRoutineGeminiResponse(aiJsonResponse);
        } catch (Exception e) {
            log.error("AI 분석 및 파싱 실패", e);
        }
        return null;
    }
}
