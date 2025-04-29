package com.medeasy.common.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum AiChatErrorCode implements ErrorCodeIfs{

    OK(200, 200, "성공"),

    BAD_REQEUST(HttpStatus.BAD_REQUEST.value(), 400, "잘못된 요청"),

    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), 500, "서버 에러"),

    NULL_POINT(HttpStatus.INTERNAL_SERVER_ERROR.value(), 512, "Null Point"),

    INVALID_FORMAT(HttpStatus.BAD_REQUEST.value(), 400, "올바르지 않은 메시지 형식입니다."),

    SESSION_USER_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR.value(), 500, "세션에 사용자 정보가 없습니다."),


    ;

    private final Integer httpStatusCode;

    private final Integer errorCode; // 커스텀 코드

    private final String description;
}
