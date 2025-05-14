package com.medeasy.domain.medicine.dto;

import lombok.*;

import java.util.List;

/**
 * item_seq 지정한 의약품과 현재 복용 중인 의약품들을 비교하여 병용 금기 약품 정보를 포함한 응답 dto
 * */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicineContraindicationResponse {

    private String pregnancyContraindication;

    private String elderlyPrecaution;

    private List<CombinationContraindication> combinationContraindications;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CombinationContraindication{
        private String itemName;

        private String itemSeq;

        private List<Long> routineGroupIds; // 동일한 약에 대해서 현재 복용 루틴이 두개 이상 있을 경우를 대비

        private String prohbtContent; // prohibited content 금지 내용
    }
}
