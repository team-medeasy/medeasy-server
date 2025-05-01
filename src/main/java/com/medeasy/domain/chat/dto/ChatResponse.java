package com.medeasy.domain.chat.dto;

import com.medeasy.domain.chat.action.ClientAction;
import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatResponse {
    private ClientAction clientAction;
    private String message;
    private List<Action> actions;

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Action{
        private String label;
        private String requestType;
    }
}