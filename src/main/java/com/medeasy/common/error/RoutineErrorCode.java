package com.medeasy.common.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RoutineErrorCode implements ErrorCodeIfs{

    OK(200, 200, "루틴 관련 요청 성공"),
    NOT_FOUND_ROUTINE(404, 404, "해당되는 루틴이 존재하지 않습니다.")
    ;

    private final Integer httpStatusCode;

    private final Integer errorCode; //내부 코드

    private final String description;
}
