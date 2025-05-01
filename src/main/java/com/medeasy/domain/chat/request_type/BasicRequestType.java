package com.medeasy.domain.chat.request_type;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 기본 요청 판별 데이터
 * */
@Getter
@AllArgsConstructor
public enum BasicRequestType implements RequestTypeIfs{
    ROUTINE_REGISTER("R1","ROUTINE_REGISTER","복약 루틴 등록", "사용자가 직접 의약품 이름, 복용량, 시간 간격 등을 설정하여 복약 루틴 등록을 요청할 때 (예: '새로운 약 복용 알림 설정할래', '아침, 점심, 저녁 약 먹는 시간 등록해 줘')", "어떤 약을 언제, 얼마나 드셔야 하나요? 자세한 복약 정보를 알려주세요."),
    PRESCRIPTION_ROUTINE_REGISTER("R3", "PRESCRIPTION_ROUTINE_REGISTER", "처방전 루틴 등록", "처방전 사진을 촬영하여 복약 루틴 일괄 등록을 요청할 때 (예: '처방전 사진으로 약 알림 등록할게', '병원 처방전 보고 복약 스케줄 만들어 줘')", "처방전 사진을 올려주시면 자동으로 복약 일정을 등록해 드릴게요."),
    MEDICINE_SEARCH("S1", "MEDICINE_SEARCH", "의약품 검색", "특정 의약품의 이름이나 효능, 부작용 등의 정보 검색을 요청할 때 (예: '타이레놀 효능 알려줘', '이 약 부작용이 뭐야?', '먹고 있는 약 검색해 줘')", "어떤 의약품에 대해 알고 싶으신가요? 약 이름을 알려주세요."),
    PILLS_PHOTO_ROUTINE_REGISTER("R4", "PILLS_PHOTO_ROUTINE_REGISTER","의약품 사진 루틴 등록", "알약 사진을 통해 루틴 등록을 요청할 때 (예: '약 사진으로 루틴 등록할래', '가지고 있는 약 사진으로 관리하고 싶어')", "가지고 있는 약 사진을 올려주세요."),
//    COMPLETED("", "", "", "", "")
    ;

    private final String code;
    private final String type;
    private final String summary;
    private final String condition;
    private final String recommendMessage;
}
