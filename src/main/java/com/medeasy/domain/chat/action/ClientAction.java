package com.medeasy.domain.chat.action;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ClientAction {
    LIST("기능 리스트업", "채팅 초반에 사용자에게 기능 리스트 보여주기")
    ;

    private String title;
    private String description;
}
