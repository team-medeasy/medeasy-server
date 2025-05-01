package com.medeasy.domain.chat.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medeasy.common.api.Api;
import com.medeasy.domain.chat.db.UserSession;
import com.medeasy.domain.chat.dto.AiChatResponse;
import com.medeasy.domain.chat.dto.ChatResponse;
import com.medeasy.domain.chat.dto.RoutineAiChatResponse;
import com.medeasy.domain.chat.parser.GeminiResponseParser;
import com.medeasy.domain.chat.analyzer.PromptAnalyzer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;

@Component
@RequiredArgsConstructor
@Slf4j
public class RoutineChatAiService {

    private final GeminiResponseParser geminiResponseParser;
    private final ObjectMapper objectMapper;

    public String registerDefaultRoutine(PromptAnalyzer promptAnalyzer, UserSession userSession, String clientMessage) {
        try {
            userSession.getMessages().add(clientMessage);
            String aiJsonResponse = promptAnalyzer.analysisType(userSession, clientMessage);
            RoutineAiChatResponse routineAiChatResponse=geminiResponseParser.parseRoutineGeminiResponse(aiJsonResponse);

            userSession.setPastRequestType(routineAiChatResponse.getRequestType());

            ChatResponse chatResponse=ChatResponse.builder()
                    .message(routineAiChatResponse.getMessage())
                    .clientAction(null)
                    .build()
                    ;

            var response=Api.OK(chatResponse);
            return objectMapper.writeValueAsString(response);
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
        return null;
    }

    public void routineProcessDetailSelector(PromptAnalyzer promptAnalyzer, UserSession userSession, String clientMessage) {
//        AiChatResponse aiChatResponse=doRequest(promptAnalyzer, userSession, clientMessage);
//
//        log.info("type 판단 이유 디버깅 {}", aiChatResponse.getResponseReason());
//        ChatResponse chatResponse=ChatResponse.builder()
//                .message(aiChatResponse.getMessage())
//                .clientAction(null)
//                .requestType(aiChatResponse.getRequestType())
//                .build()
//                ;
//
//        var response= Api.OK(chatResponse);
//        String responseJson = objectMapper.writeValueAsString(response);
//        session.sendMessage(new TextMessage(responseJson));
//
//        userSession.setPastRequestType(aiChatResponse.getRequestType());
    }
}
