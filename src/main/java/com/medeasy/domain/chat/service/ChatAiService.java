package com.medeasy.domain.chat.service;

import com.medeasy.domain.chat.db.UserSession;
import com.medeasy.domain.chat.db.UserSessionRepository;
import com.medeasy.domain.chat.dto.AiChatResponse;
import com.medeasy.domain.chat.parser.GeminiResponseParser;
import com.medeasy.domain.chat.analyzer.PromptAnalyzer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatAiService {

    private final GeminiResponseParser geminiResponseParser;

    public AiChatResponse doRequest(PromptAnalyzer promptAnalyzer, UserSession userSession, String clientMessage) {
        try {
            userSession.getMessages().add(clientMessage);
            String aiJsonResponse = promptAnalyzer.analysisType(userSession, clientMessage);

            return geminiResponseParser.parseGeminiResponse(aiJsonResponse);
        } catch (Exception e) {
            log.error("AI 분석 및 파싱 실패", e);
        }
        return null;
    }
}
