package com.medeasy.domain.chat.db;

import com.medeasy.domain.chat.status.ChatStatusIfs;
import lombok.*;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSession {
    private Long userId;
    private WebSocketSession session;
    private ChatStatusIfs chatStatus;
    private int currentStep;       // ex) 루틴 등록 단계
    private Map<String, Object> tempData = new HashMap<>(); // 중간 저장 데이터
}

