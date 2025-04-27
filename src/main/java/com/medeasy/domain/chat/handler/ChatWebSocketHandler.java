package com.medeasy.domain.chat.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medeasy.common.api.Api;
import com.medeasy.common.error.AiChatErrorCode;
import com.medeasy.common.error.ErrorCode;
import com.medeasy.common.error.ErrorCodeIfs;
import com.medeasy.common.exception.ApiException;
import com.medeasy.domain.ai.dto.AiChatResponse;
import com.medeasy.domain.ai.service.ChatAiService;
import com.medeasy.domain.ai.service.GeminiResponseParser;
import com.medeasy.domain.chat.action.ClientAction;
import com.medeasy.domain.chat.db.UserSession;
import com.medeasy.domain.chat.dto.ChatMessage;
import com.medeasy.domain.chat.dto.ChatResponse;
import com.medeasy.domain.chat.message_creator.BasicMessageCreator;
import com.medeasy.domain.chat.status.SuperStatus;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper;
    private final Map<Long, UserSession> userSessions = new ConcurrentHashMap<>(); // 사용자별 세션 상태 관리

    private final ChatAiService chatAiService;
    private final GeminiResponseParser geminiResponseParser;

    private final BasicMessageCreator basicMessageCreator;

    public ChatWebSocketHandler(
            ObjectMapper objectMapper,
            ChatAiService chatAiService,
            GeminiResponseParser geminiResponseParser,
            BasicMessageCreator basicMessageCreator
        )
    {
        this.objectMapper = objectMapper;
        this.chatAiService = chatAiService;
        this.geminiResponseParser = geminiResponseParser;
        this.basicMessageCreator = basicMessageCreator;
    }

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) {
        // 사용자 정보 조회
        Optional<Long> userId=getUserId(session);

        if(userId.isEmpty()) {
            try {
                sendError(session, AiChatErrorCode.SESSION_USER_NOT_FOUND);
            } catch (IOException e) {
                log.info("세션에 사용자 정보 존재 x ");
            }
        }

        // 사용자 세션 저장
        UserSession userSession = UserSession.builder()
                        .userId(userId.get())
                        .session(session)
                        .chatStatus(SuperStatus.BASIC)
                        .build()
                        ;
        userSessions.put(userId.get(), userSession);

        // 응답 생성
        ChatResponse response = ChatResponse.builder()
                    .clientAction(ClientAction.LIST)
                    .message(basicMessageCreator.helloMessage(userId.get()))
                    .build()
                    ;

        sendMessage(session, response);
    }

    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, TextMessage message) throws Exception {
        // 사용자가 메시지 보냈을 때
        String payload = message.getPayload();
        try {
            ChatMessage chatMessage = objectMapper.readValue(payload, ChatMessage.class);
            processMessage(chatMessage, session);
        } catch (Exception e) {
            log.error("메시지 파싱 실패: {}", payload, e);
            sendError(session, AiChatErrorCode.INVALID_FORMAT);
        }
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) {
        // 연결 끊겼을 때
        Long userId=getUserId(session).orElseThrow(() -> new ApiException(ErrorCode.SERVER_ERROR, "userId is missing in session attributes"));
        userSessions.remove(userId);
    }

    /**
     * 채팅에 대한 타입이 존재하는 것이지 (basic, routine, schedule)
     *
     * 제일 처음은 basic 타입
     *
     * 사용자의 채팅으로 무엇을 원하는 지 유추하고 type을 지정해주는 것
     * */
    private void processMessage(ChatMessage chatMessage, WebSocketSession session) throws IOException {
        Long userId=getUserId(session).orElseThrow(() -> new ApiException(ErrorCode.SERVER_ERROR, "userId is missing in session attributes"));
        UserSession userSession=userSessions.get(userId);

        try {
            // 1. 사용자 메시지를 AI로 분석
            String aiJsonResponse = chatAiService.analysisType(chatMessage.getMessage());

            // 2. AI 응답 파싱 (AiChatResponse 객체)
            AiChatResponse aiChatResponse = geminiResponseParser.parseGeminiResponse(aiJsonResponse);

            // 3. 최종 응답 구성
            Api<AiChatResponse> chatResponse=Api.OK(aiChatResponse);

            // 4. 클라이언트에 전송
            String responseJson = objectMapper.writeValueAsString(chatResponse);
            session.sendMessage(new TextMessage(responseJson));

        } catch (Exception e) {
            log.error("AI 분석 및 파싱 실패", e);
            sendError(session, "AI 분석 중 오류가 발생했습니다.");
        }
    }

    private void sendError(WebSocketSession session, ErrorCodeIfs errorCode) throws IOException {
        Api<Object> response = Api.ERROR(errorCode);
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
    }

    private void sendError(WebSocketSession session) throws IOException {
        Api<Object> response = Api.ERROR(AiChatErrorCode.SERVER_ERROR);
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
    }

    private void sendError(WebSocketSession session, String errorMessage) throws IOException {
        Api<Object> response = Api.ERROR(AiChatErrorCode.SERVER_ERROR, errorMessage);
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
    }

    private void sendMessage(WebSocketSession session, ChatResponse message) {
        Api<Object> response = Api.OK(message);

        try {
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
        } catch (IOException e) {
            log.info("메시지 전송 중 오류", e);
        }
    }

    protected Optional<Long> getUserId(WebSocketSession session) {
        return Optional.ofNullable(session.getAttributes().get("userId"))
                .map(id -> {
                    if (id instanceof Integer) {
                        return ((Integer) id).longValue();
                    } else if (id instanceof Long) {
                        return (Long) id;
                    } else {
                        throw new ApiException(ErrorCode.BAD_REQEUST, "Invalid userId type: " + id.getClass());
                    }
                });
    }
}

