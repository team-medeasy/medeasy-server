package com.medeasy.common.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MedicineErrorCode implements ErrorCodeIfs{

    OK(200, 200, "약 관련 처리 성공"),
    NOT_FOUND_MEDICINE(404, 404, "해당되는 약이 존재하지 않습니다.")
    ;

    private final Integer httpStatusCode;

    private final Integer errorCode; //내부 코드

    private final String description;
}
