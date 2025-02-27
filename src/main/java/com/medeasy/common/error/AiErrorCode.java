package com.medeasy.common.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

// 3000번대
@Getter
public enum AiErrorCode implements ErrorCodeIfs{

    PARSING_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), 3001, "ai 로직 파싱 중 오류")
    ;

    private final Integer httpStatusCode;

    private final Integer errorCode; // 커스텀 코드

    private final String description;

    private AiErrorCode(Integer httpStatusCode, Integer errorCode, String description) {
        this.httpStatusCode = httpStatusCode;
        this.errorCode = errorCode;
        this.description = description;
    }
}
