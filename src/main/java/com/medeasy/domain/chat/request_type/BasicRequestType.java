package com.medeasy.domain.chat.request_type;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 기본 요청 판별 데이터
 * */
@Getter
@AllArgsConstructor
public enum BasicRequestType implements RequestTypeIfs{
    ROUTINE_REGISTER("R1","ROUTINE_REGISTER","루틴 등록", "사용자가 정확히 루틴 등록 정보를 제공하지 않고, 일반적으로 루틴을 등록하고 싶다고 할 때"),
    DEFAULT_ROUTINE_REGISTER("R2", "DEFAULT_ROUTINE_REGISTER","기본 루틴 등록", "사용자에게 루틴 관련 정보를 질의 후 등록"),
    PRESCRIPTION_ROUTINE_REGISTER("R3", "PRESCRIPTION_ROUTINE_REGISTER", "처방전 루틴 등록", "처방전 사진을 통한 루틴 등록"),
    PILLS_PHOTO_ROUTINE_REGISTER("R4", "PILLS_PHOTO_ROUTINE_REGISTER","의약품 사진 루틴 등록", "알약 사진을 통한 루틴 등록"),

    MEDICINE_SEARCH("S1", "MEDICINE_SEARCH", "의약품 검색", "사용자의 의약품 정보 입력을 통한 약품 서칭")
    ;

    private final String code;
    private final String type;
    private final String summary;
    private final String condition;
}
