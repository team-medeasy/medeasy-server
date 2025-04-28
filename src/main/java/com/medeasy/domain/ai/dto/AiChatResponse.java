package com.medeasy.domain.ai.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiChatResponse {
    private String  requestType;
    private String message;
}
