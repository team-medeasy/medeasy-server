package com.medeasy.domain.medicine.dto;

import lombok.Data;

@Data
public class MedicineRequest {
    private String entpName;             // 업체명
    private String itemName;             // 약 이름
    private String itemSeq;              // 약 코드 (숫자 문자열)
    private String efcyQesitm;           // 효능
    private String useMethodQesitm;      // 사용법
    private String atpnWarnQesitm;       // 주의 경고 (필요 시 활용)
    private String atpnQesitm;           // 주의사항
    private String intrcQesitm;          // 상호작용
    private String seQesitm;             // 부작용
    private String depositMethodQesitm;  // 보관법
    private String openDe;               // 공개일 (예:"2021-01-29 00:00:00")
    private String updateDe;             // 수정일 (예:"2024-05-09")
    private String itemImage;            // 이미지 URL
    private String bizrno;               // business number
}
