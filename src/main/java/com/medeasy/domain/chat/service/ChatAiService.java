package com.medeasy.domain.chat.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medeasy.common.api.Api;
import com.medeasy.domain.chat.db.UserSession;
import com.medeasy.domain.chat.db.UserSessionRepository;
import com.medeasy.domain.chat.dto.AiChatResponse;
import com.medeasy.domain.chat.dto.ChatResponse;
import com.medeasy.domain.chat.parser.GeminiResponseParser;
import com.medeasy.domain.chat.analyzer.PromptAnalyzer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatAiService {

    private final GeminiResponseParser geminiResponseParser;
    private final ObjectMapper objectMapper;

    public AiChatResponse doRequest(PromptAnalyzer promptAnalyzer, UserSession userSession, String clientMessage) {
        try {
            userSession.getMessages().add(clientMessage);
            String aiJsonResponse = promptAnalyzer.analysisType(userSession, clientMessage);
            log.info("ai 응답 형식 로그: {}", aiJsonResponse);

            return geminiResponseParser.parseGeminiResponse(aiJsonResponse);
        } catch (Exception e) {
            log.error("AI 분석 및 파싱 실패", e);
        }
        return null;
    }

//    /**
//     * 사용자의 초기 요청 분석하는 메서드
//     * */
//    public String analyzerRequest(PromptAnalyzer promptAnalyzer, UserSession userSession, String clientMessage) {
//        AiChatResponse aiChatResponse=doRequest(promptAnalyzer, userSession, clientMessage);
//
//        ChatResponse chatResponse=ChatResponse.builder()
//                .message(aiChatResponse.getMessage())
//                .clientAction(null)
//                .requestType(aiChatResponse.getRequestType())
//                .build()
//                ;
//
//        var response= Api.OK(chatResponse);
//
//        String responseJson;
//        try {
//            objectMapper.writeValueAsString(response);
//        } catch (Exception e) {
//            return "사용자의 요청을 처리하는 중 오류가 발생하였습니다.";
//        }
//        userSession.setPastRequestType(aiChatResponse.getRequestType());
//
//        return responseJson;
//    }
}
