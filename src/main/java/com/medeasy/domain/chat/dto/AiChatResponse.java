package com.medeasy.domain.chat.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
/**
 * 응답 객체 기준으로 ai 응답 파싱하기 때문에, 이 부분만 수정해줘도 됨.
 * */
public class AiChatResponse {
    private String requestType;
    private String message;
    private String responseReason;
}
