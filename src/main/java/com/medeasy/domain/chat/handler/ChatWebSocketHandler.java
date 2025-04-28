package com.medeasy.domain.chat.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medeasy.common.api.Api;
import com.medeasy.common.error.AiChatErrorCode;
import com.medeasy.common.error.ErrorCode;
import com.medeasy.common.error.ErrorCodeIfs;
import com.medeasy.common.exception.ApiException;
import com.medeasy.domain.ai.dto.AiChatResponse;
import com.medeasy.domain.chat.analyzer.BasicPromptAnalyzer;
import com.medeasy.domain.chat.analyzer.PromptAnalyzer;
import com.medeasy.domain.ai.service.GeminiResponseParser;
import com.medeasy.domain.chat.action.ClientAction;
import com.medeasy.domain.chat.analyzer.RoutinePromptAnalyzer;
import com.medeasy.domain.chat.converter.RequestTypeConverter;
import com.medeasy.domain.chat.db.UserSession;
import com.medeasy.domain.chat.dto.ChatMessage;
import com.medeasy.domain.chat.dto.ChatResponse;
import com.medeasy.domain.chat.message_creator.BasicMessageCreator;
import com.medeasy.domain.chat.request_type.BasicRequestType;
import com.medeasy.domain.chat.request_type.RequestTypeIfs;
import com.medeasy.domain.chat.request_type.RoutineRequestType;
import com.medeasy.domain.chat.service.ChatAiService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper;
    private final Map<Long, UserSession> userSessions = new ConcurrentHashMap<>(); // 사용자별 세션 상태 관리

    private final ChatAiService chatAiService;
    private final RequestTypeConverter requestTypeConverter;


    private final BasicMessageCreator basicMessageCreator;

    private final BasicPromptAnalyzer basicPromptAnalyzer;
    private final RoutinePromptAnalyzer routinePromptAnalyzer;

    public ChatWebSocketHandler(
            ObjectMapper objectMapper,
            RequestTypeConverter requestTypeConverter,
            BasicMessageCreator basicMessageCreator,
            ChatAiService chatAiService,
            BasicPromptAnalyzer basicPromptAnalyzer,
            RoutinePromptAnalyzer routinePromptAnalyzer
        )
    {
        this.objectMapper = objectMapper;
        this.requestTypeConverter = requestTypeConverter;
        this.chatAiService = chatAiService;
        this.basicMessageCreator = basicMessageCreator;
        this.basicPromptAnalyzer = basicPromptAnalyzer;
        this.routinePromptAnalyzer = routinePromptAnalyzer;
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
                        .pastRequestType(null)
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

        /**
         * 사용자가 수행할 기능이 정해지지 않은 경우
         * */
        if (userSession.getPastRequestType() == null){
            AiChatResponse aiChatResponse=chatAiService.doRequest(basicPromptAnalyzer, chatMessage.getMessage());

            ChatResponse chatResponse=ChatResponse.builder()
                    .message(aiChatResponse.getMessage())
                    .clientAction(null)
                    .build()
                    ;

            var response=Api.OK(chatResponse);
            String responseJson = objectMapper.writeValueAsString(response);
            session.sendMessage(new TextMessage(responseJson));

            RequestTypeIfs requestType = requestTypeConverter.mapBasicRequestType(aiChatResponse.getRequestType());
            userSession.setPastRequestType(requestType);

        }
        /**
         * 루틴 등록 기능은 선택하였으나, 디테일한 기능이 정해지지 않은 경우
         * */
        else if (userSession.getPastRequestType() == BasicRequestType.ROUTINE_REGISTER) {
            AiChatResponse aiChatResponse=chatAiService.doRequest(routinePromptAnalyzer, chatMessage.getMessage());

            ChatResponse chatResponse=ChatResponse.builder()
                    .message(aiChatResponse.getMessage())
                    .clientAction(null)
                    .build()
                    ;

            var response=Api.OK(chatResponse);
            String responseJson = objectMapper.writeValueAsString(response);
            session.sendMessage(new TextMessage(responseJson));

            RequestTypeIfs requestType = requestTypeConverter.mapRoutineRequestType(aiChatResponse.getRequestType());
            userSession.setPastRequestType(requestType);
        }
        /**
         * 기본 루틴 등록을 할 경우
         *
         * 1. 사용자의 메시지로부터 복약 정보 추출
         * 2. 사용자 복약정보 context와 메시지 비교 대입
         * 3. context 리턴
         * 4. context 조건을 전부 만족한 경우 루틴 등록
         * 5. 아닌 경우는 message 출력하도록 프롬프트
         * */
        else if (userSession.getPastRequestType() == BasicRequestType.DEFAULT_ROUTINE_REGISTER) {
            // TODO 기본 루틴 등록 로직 사용자의 채팅 메시지에 루틴 등록에 충분한 데이터가 있는지 검사
            AiChatResponse aiChatResponse=chatAiService.doRequest(routinePromptAnalyzer, chatMessage.getMessage());

            ChatResponse chatResponse=ChatResponse.builder()
                    .message(aiChatResponse.getMessage())
                    .clientAction(null)
                    .build()
                    ;

            var response=Api.OK(chatResponse);
            String responseJson = objectMapper.writeValueAsString(response);
            session.sendMessage(new TextMessage(responseJson));

            RequestTypeIfs requestType = requestTypeConverter.mapRoutineRequestType(aiChatResponse.getRequestType());
            userSession.setPastRequestType(requestType);
        } else {
            sendError(session, AiChatErrorCode.INVALID_FORMAT);
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

