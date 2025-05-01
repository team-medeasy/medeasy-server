package com.medeasy.domain.chat.service;

import com.medeasy.domain.chat.db.UserSession;
import com.medeasy.domain.chat.dto.RoutineAiChatResponse;
import com.medeasy.domain.chat.parser.GeminiResponseParser;
import com.medeasy.domain.chat.analyzer.PromptAnalyzer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RoutineChatAiService {

    private final GeminiResponseParser geminiResponseParser;

    public RoutineAiChatResponse registerDefaultRoutine(PromptAnalyzer promptAnalyzer, UserSession userSession, String clientMessage) {
        try {
            userSession.getMessages().add(clientMessage);
            String aiJsonResponse = promptAnalyzer.analysisType(userSession, clientMessage);

            return geminiResponseParser.parseRoutineGeminiResponse(aiJsonResponse);
        } catch (Exception e) {
            log.error("AI 분석 및 파싱 실패", e);
        }
        return null;
    }

    // TODO 처방전 루틴 등록
    public RoutineAiChatResponse registerPrescriptionRoutine() {
        return null;
    }

    // TODO 알약 사진 루틴 등록
    public RoutineAiChatResponse registerPillsPhotoRoutine() {
        return null;
    }

    private String responseMessageCreator(UserSession userSession) {
        UserSession.RoutineContext routineContext=userSession.getRoutineContext();
        if (routineContext.getMedicineName() != null) {

        }
    }
}
