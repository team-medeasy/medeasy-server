package com.medeasy.domain.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 클라이언트 → 서버로 오는 WebSocket 메시지 데이터 모델
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequest {

    private String requestType; // 요청 타입 (ex: REGISTER_ROUTINE, VIEW_SCHEDULE 등)
    private String message;
}

