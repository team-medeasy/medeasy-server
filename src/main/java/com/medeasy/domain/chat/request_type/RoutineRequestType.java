package com.medeasy.domain.chat.request_type;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 루틴 등록 특화 요청 타입 판별 데이터
 * */
@Getter
@AllArgsConstructor
public enum RoutineRequestType implements RequestTypeIfs{
    DEFAULT_ROUTINE_REGISTER("R2", "DEFAULT_ROUTINE_REGISTER","기본 루틴 등록", "사용자에게 루틴 관련 정보를 질의 후 등록"),
    PRESCRIPTION_ROUTINE_REGISTER("R3", "PRESCRIPTION_ROUTINE_REGISTER", "처방전 루틴 등록", "처방전 사진을 통한 루틴 등록"),
    PILLS_PHOTO_ROUTINE_REGISTER("R4", "PILLS_PHOTO_ROUTINE_REGISTER","의약품 사진 루틴 등록", "알약 사진을 통한 루틴 등록"),
    ;

    private final String code;
    private final String type;
    private final String summary;
    private final String condition;
}
