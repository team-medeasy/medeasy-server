package com.medeasy.domain.chat.dto;

import com.medeasy.domain.chat.action.ClientAction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatResponse {

    private ClientAction clientAction;
    private String message;
}