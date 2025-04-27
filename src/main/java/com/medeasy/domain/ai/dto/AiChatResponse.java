package com.medeasy.domain.ai.dto;

import com.medeasy.domain.chat.request_type.RequestType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiChatResponse {
    private RequestType requestType;
    private String message;
}
