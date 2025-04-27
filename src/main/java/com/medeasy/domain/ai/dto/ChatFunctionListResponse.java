package com.medeasy.domain.ai.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatFunctionListResponse {

    // 기능 식별자
    private String id;

    // 기능 이름
    private String functionName;

    // 기능 호출 엔드포인트
    private String functionEndPoint;
}
