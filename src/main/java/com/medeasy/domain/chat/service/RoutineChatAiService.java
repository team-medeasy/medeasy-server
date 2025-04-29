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
            userSession.getMessages().add(clientMessage);
            String aiJsonResponse = promptAnalyzer.analysisType(userSession, clientMessage);

            return geminiResponseParser.parseRoutineGeminiResponse(aiJsonResponse);
        } catch (Exception e) {
            log.error("AI 분석 및 파싱 실패", e);
        }
        return null;
    }
}
