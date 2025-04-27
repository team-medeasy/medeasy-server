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
public class ChatMessage {

    private String type; // 요청 타입 (ex: REGISTER_ROUTINE, VIEW_SCHEDULE 등)
    private Integer step; // 루틴 등록 등 단계별 흐름을 관리할 수 있는 필드 (nullable)
    private String data; // 실제 입력 데이터 (ex: 처방전 업로드, 복용 시간, 약 이름 등)
    private String message;
}

