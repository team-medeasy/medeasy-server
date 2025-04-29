package com.medeasy.domain.chat.request_type;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 루틴 등록 특화 요청 타입 판별 데이터
 * */
@Getter
@AllArgsConstructor
public enum RoutineRequestType implements RequestTypeIfs{
    DEFAULT_ROUTINE_REGISTER(
            "R2",
            "DEFAULT_ROUTINE_REGISTER",
            "기본 루틴 등록",
            "일반 루틴 등록을 요청한 경우",
            "복약 정보를 입력해주세요. 약 이름, 하루 복약 시간대(아침, 점심, 저녁), 약 개수, 복용 간격 등",
            ""
    ),
    PRESCRIPTION_ROUTINE_REGISTER(
            "R3",
            "PRESCRIPTION_ROUTINE_REGISTER",
            "처방전 루틴 등록",
            "사용자가 처방전이라는 단어를 사용하거나, 병원에서 받은 처방전 사진을 통한 루틴 등록을 요청하는 경우",
            "처방전 사진 첨부 및 촬영해주세요!",
            ""
    ),
    PILLS_PHOTO_ROUTINE_REGISTER(
            "R4",
            "PILLS_PHOTO_ROUTINE_REGISTER",
            "의약품 사진 루틴 등록",
            "사용자가 알약 사진, 약 사진이라는 단어를 사용하여 루틴 등록을 요청하는 경우",
            "알약 사진 첨부 및 촬영해주세요!",
            ""
    )
    ;

    private final String code;
    private final String type;
    private final String summary;
    private final String condition;
    private final String recommendMessage;
    private final String nextStepCondition;
}
