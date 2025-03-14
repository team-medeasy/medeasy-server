package com.medeasy.common.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserScheduleErrorCode implements ErrorCodeIfs{

    OK(200, 200, "사용자 스케줄 등록 요청 성공"),
    NOT_FOUND_USER_SCHEDULE(404, 404, "해당되는 스케줄이 존재하지 않습니다.")
    ;


    private final Integer httpStatusCode;

    private final Integer errorCode; //내부 코드

    private final String description;
}
