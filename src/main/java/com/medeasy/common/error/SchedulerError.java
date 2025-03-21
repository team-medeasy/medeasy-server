package com.medeasy.common.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum SchedulerError implements ErrorCodeIfs{

    OK(200, 200, "성공"),

    BAD_REQEUST(HttpStatus.BAD_REQUEST.value(), 400, "잘못된 요청"),

    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), 500, "서버 에러"),

    NULL_POINT(HttpStatus.INTERNAL_SERVER_ERROR.value(), 512, "Null Point"),

    NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR.value(), 404, "스케줄을 찾을 수 없습니다."),

    FOREIGN_KEY_CONSTRAINT(HttpStatus.CONFLICT.value(), 409, "삭제할 수 없습니다. 스케줄에 등록된 루틴이 존재합니다."),
    ;

    private final Integer httpStatusCode;

    private final Integer errorCode; // 커스텀 코드

    private final String description;
}
