package com.medeasy.domain.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Builder;

/**
 * 서버 → 클라이언트로 보내는 WebSocket 응답 메시지 모델
 */
@Getter
@Builder
@AllArgsConstructor
public class ChatResponse<T> {
    private String type; // "SUCCESS" or "ERROR"
    private T data;

    public static <T> ChatResponse<T> success(T data) {
        return ChatResponse.<T>builder()
                .type("RESPONSE")
                .data(data)
                .build();
    }

    public static ChatResponse<String> error(String message) {
        return ChatResponse.<String>builder()
                .type("ERROR")
                .data(message)
                .build();
    }
}