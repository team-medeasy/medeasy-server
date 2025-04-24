package com.medeasy.common.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

// 3000번대
@Getter
public enum TtsErrorCode implements ErrorCodeIfs{

    PARSING_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), 4001, "tts 로직 파싱 중 오류"),

    GCP_TTS_REQUEST_ERROR(500, 4001, "tts 요청 중 오류 발생")
    ;

    private final Integer httpStatusCode;

    private final Integer errorCode; // 커스텀 코드

    private final String description;

    private TtsErrorCode(Integer httpStatusCode, Integer errorCode, String description) {
        this.httpStatusCode = httpStatusCode;
        this.errorCode = errorCode;
        this.description = description;
    }
}
