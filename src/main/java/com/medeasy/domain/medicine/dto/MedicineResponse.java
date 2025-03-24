package com.medeasy.domain.medicine.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicineResponse {

    private String id;

    private String itemSeq; // 의약품 코드

    private String itemName; // 의약품 이름

    private String entpName; // 제조사 이름

    private String entpSeq; // 제조사 코드

    private String chart; // 성상

    private String classNo; // 분류 코드

    private String className; // 분류명

    private String ediCode; // 보험코드

    private String drugShape; // 의약품 모양

    private String colorClasses; // 색상

    private String formCodeName; // 제형 코드

    private String lineFront; // 앞면 분할선

    private String lineBack; // 뒷면 분할선

    private String printFront; // 앞면 표기

    private String printBack; // 뒷면 표기

    private String markCodeFrontAnal; // 앞면 마크코드 분석값

    private String markCodeBackAnal; // 뒷면 마크코드 분석값

    private String indications; // 효능 및 효과

    private String dosage; // 복용 방법

    private String precautions; // 주의사항

    private String sideEffects; // 부작용

    private String storageMethod; // 보관 방법

    private String validTerm; // 유효 기간

    private String etcOtcName; // 전문의약품 여부

    private String cancelName; // 판매 상태

    private String itemImage; // 이미지 URL

    private Float lengLong; // 길이 (긴쪽)

    private Float lengShort; // 길이 (짧은쪽)

    private Float thick; // 두께

    private Long isPill; // 정제 여부 (0,1)
}
