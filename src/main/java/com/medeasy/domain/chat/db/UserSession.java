package com.medeasy.domain.chat.db;

import com.medeasy.domain.chat.dto.RoutineAiChatResponse;
import com.medeasy.domain.chat.status.ChatStatusIfs;
import lombok.*;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSession {
    private Long userId;
    private WebSocketSession session;
    private ChatStatusIfs chatStatus;
    private String pastRequestType;       // ex) 루틴 등록 단계
    private List<String> messages;// 중간 저장 데이터
    private RoutineContext routineContext;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RoutineContext{
        private String medicineName;
        private Integer intervalDays;
        private Integer dose;
        private List<String> scheduleNames;
    }

}

