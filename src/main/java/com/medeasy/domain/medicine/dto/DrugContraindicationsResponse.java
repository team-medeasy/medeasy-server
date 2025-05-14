package com.medeasy.domain.medicine.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DrugContraindicationsResponse {

    private String itemSeq;

    private String pregnancyContraindication; // 임산부 금기 사항

    private String elderlyPrecaution; // 노인 금기 사항

    private List<CombinationContraindication> combinationContraindications;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CombinationContraindication {
        private String mixtureItemSeq; // 병용 약품

        private String prohbtContent; // 금지 내용
    }
}
